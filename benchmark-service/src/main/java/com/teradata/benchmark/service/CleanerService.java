/*
 * Copyright 2013-2015, Teradata, Inc. All rights reserved.
 */
package com.teradata.benchmark.service;

import com.teradata.benchmark.service.model.BenchmarkRun;
import com.teradata.benchmark.service.repo.BenchmarkRunRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

import static com.teradata.benchmark.service.model.Status.FAILED;
import static com.teradata.benchmark.service.utils.TimeUtils.currentDateTime;

@Service
public class CleanerService
{
    private static final Logger LOG = LoggerFactory.getLogger(CleanerService.class);

    public static final long BENCHMARK_TIMEOUT_HOURS = 24;

    @Autowired
    private BenchmarkRunRepo benchmarkRunRepo;

    @Transactional
    @Scheduled(fixedDelay = 1000 * 60 * 60)
    public void cleanUpStaleBenchmarks()
    {
        LOG.info("Cleaning up stale benchmarks");

        ZonedDateTime currentDate = currentDateTime();
        ZonedDateTime startDate = currentDate.minusHours(BENCHMARK_TIMEOUT_HOURS);
        for (BenchmarkRun benchmarkRun : benchmarkRunRepo.findSartedBefore(startDate)) {
            LOG.info("Failing stale benchmark - {}", benchmarkRun);
            benchmarkRun.setEnded(currentDate);
            benchmarkRun.setStatus(FAILED);
            benchmarkRunRepo.save(benchmarkRun);
        }
    }
}
