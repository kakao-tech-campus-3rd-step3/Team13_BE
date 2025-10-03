package com.b4f2.pting.matchingsystem;

import java.util.List;
import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.b4f2.pting.matchingsystem.algorithm.MatchingAlgorithm;
import com.b4f2.pting.matchingsystem.simulation.TestScenarioRunner;

@SpringBootApplication
public class MatchingSystemApplication implements CommandLineRunner {

    private final TestScenarioRunner runner;
    private final List<MatchingAlgorithm> algorithms;

    public static void main(String[] args) {
        SpringApplication.run(MatchingSystemApplication.class, args);
    }

    public MatchingSystemApplication(TestScenarioRunner runner, List<MatchingAlgorithm> algorithms) {
        this.runner = runner;
        this.algorithms = algorithms;
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Ranking Game에 참여할 Player 수 입력: ");
        int count = scanner.nextInt();

        System.out.println("반복 입력: ");
        int rounds = scanner.nextInt();

        runner.runAllScenarios(algorithms, count, rounds);
    }
}

