/*
 * Copyright 2013-2015, Teradata, Inc. All rights reserved.
 */
package com.teradata.benchmark.driver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.StreamSupport;

import static com.google.common.io.Files.getNameWithoutExtension;
import static java.lang.ClassLoader.getSystemClassLoader;
import static java.nio.file.Files.newDirectoryStream;
import static java.nio.file.Files.readAllBytes;
import static java.util.stream.Collectors.toList;

@Component
public class BenchmarkQueryLoader
{

    @Autowired
    private BenchmarkProperties properties;

    public List<BenchmarkQuery> loadBenchmarkQueries()
    {

        try (DirectoryStream<Path> sqlFiles = newDirectoryStream(sqlFilesPath(), "*.sql")) {
            return StreamSupport.stream(sqlFiles.spliterator(), false)
                    .map(this::loadBenchmarkQuery)
                    .collect(toList());
        }
        catch (IOException | URISyntaxException e) {
            throw new BenchmarkExecutionException("Could not load sql files", e);
        }
    }

    private Path sqlFilesPath()
            throws URISyntaxException
    {
        URL sqlDir = getSystemClassLoader().getResource(properties.getSqlDir());
        if (sqlDir != null) {
            return Paths.get(sqlDir.toURI());
        }
        return FileSystems.getDefault().getPath(properties.getSqlDir());
    }

    private BenchmarkQuery loadBenchmarkQuery(Path path)
    {
        try {
            String name = getNameWithoutExtension(path.getFileName().toString());
            String sqlStatement = new String(readAllBytes(path));

            if (sqlStatement.endsWith(";")) {
                sqlStatement = sqlStatement.substring(0, sqlStatement.length() - 1);
            }

            sqlStatement = sqlStatement.replace("\n", " ");

            return new BenchmarkQuery(name, sqlStatement);
        }
        catch (IOException e) {
            throw new BenchmarkExecutionException("Could not load path: " + path, e);
        }
    }
}