/*
 * Copyright 2013-2015, Teradata, Inc. All rights reserved.
 */
package com.teradata.benchmark.driver;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.teradata.benchmark.driver.concurrent.ExecutorServiceFactory;
import com.teradata.benchmark.driver.listeners.benchmark.BenchmarkStatusReporter;
import com.teradata.benchmark.driver.macro.MacroService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import static com.facebook.presto.jdbc.internal.guava.util.concurrent.MoreExecutors.newDirectExecutorService;
import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class TestConfig
{

    @Primary
    @Bean
    public TaskExecutor defaultTaskExecutor()
    {
        // MockRestServiceServer expects calls in particular order,
        // we need to use sync task executor
        return new SyncTaskExecutor();
    }

    @Bean(name = "prewarmStatusReporter")
    public BenchmarkStatusReporter prewarmStatusReporter()
    {
        return mock(BenchmarkStatusReporter.class);
    }

    @Bean
    public MacroService macroService(MacroService macroService)
    {
        return spy(macroService);
    }

    @Primary
    @Bean
    public ExecutorServiceFactory getDirectTestExecutorServiceFactory()
    {
        return new ExecutorServiceFactory()
        {
            @Override
            public ListeningExecutorService create(int concurrency)
            {
                // no concurrency in tests
                return listeningDecorator(newDirectExecutorService());
            }
        };
    }
}
