/*
 * Copyright 2013-2015, Teradata, Inc. All rights reserved.
 */
package com.teradata.benchmark.service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import java.util.Objects;
import java.util.Set;

import static com.google.common.base.MoreObjects.toStringHelper;

@Entity
@Table(name = "benchmarks", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "sequence_id"}))
public class Benchmark
{

    @Id
    @SequenceGenerator(name = "benchmarks_id_seq",
            sequenceName = "benchmarks_id_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "benchmarks_id_seq")
    @Column(name = "id")
    @JsonIgnore
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "sequence_id")
    private String sequenceId;

    @OneToMany(mappedBy = "benchmark", cascade = CascadeType.ALL)
    private Set<Execution> executions;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "benchmark_measurements",
            joinColumns = @JoinColumn(name = "benchmark_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "measurement_id", referencedColumnName = "id"))
    private Set<Measurement> measurements;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getSequenceId()
    {
        return sequenceId;
    }

    public void setSequenceId(String sequenceId)
    {
        this.sequenceId = sequenceId;
    }

    public Set<Execution> getExecutions()
    {
        return executions;
    }

    public void setExecutions(Set<Execution> executions)
    {
        this.executions = executions;
    }

    public Set<Measurement> getMeasurements()
    {
        return measurements;
    }

    public void setMeasurements(Set<Measurement> measurements)
    {
        this.measurements = measurements;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Benchmark benchmark = (Benchmark) o;
        return Objects.equals(name, benchmark.name) &&
                Objects.equals(sequenceId, benchmark.sequenceId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), name, sequenceId);
    }

    @Override
    public String toString()
    {
        return toStringHelper(this)
                .add("name", name)
                .add("sequenceId", sequenceId)
                .add("executions", executions)
                .add("measurements", measurements)
                .toString();
    }
}