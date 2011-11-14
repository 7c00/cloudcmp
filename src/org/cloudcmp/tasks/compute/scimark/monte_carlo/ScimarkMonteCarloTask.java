/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * Copyright (c) 1997,1998 Sun Microsystems, Inc. All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */

package org.cloudcmp.tasks.compute.scimark.monte_carlo;
import java.io.PrintStream;
import java.util.Map;

import org.cloudcmp.Adaptor;
import org.cloudcmp.tasks.compute.ComputeTask;
import org.cloudcmp.tasks.compute.scimark.lu.ScimarkLUTask;
import org.cloudcmp.tasks.compute.scimark.utils.*;

/**
 * Estimate Pi by approximating the area of a circle.
 *
 * How: generate N random numbers in the unit square, (0,0) to (1,1)
 * and see how are within a radius of 1 or less, i.e.
 * <pre>
 *
 * sqrt(x^2 + y^2) < r
 *
 * </pre>
 * since the radius is 1.0, we can square both sides
 * and avoid a sqrt() computation:
 * <pre>
 *
 * x^2 + y^2 <= 1.0
 *
 * </pre>
 * this area under the curve is (Pi * r^2)/ 4.0,
 * and the area of the unit of square is 1.0,
 * so Pi can be approximated by
 * <pre>
 * # points with x^2+y^2 < 1
 * Pi =~ 		--------------------------  * 4.0
 * total # points
 *
 * </pre>
 *
 */

public class ScimarkMonteCarloTask extends ComputeTask {
	public ScimarkMonteCarloTask(Adaptor adaptor) {
		super(adaptor);
		// TODO Auto-generated constructor stub
	}
	
	public String getTaskName() {
		return "scimark.monte_carlo";
	}
	
	protected SingleComputeTask getSingleComputeTask() {
		return new SingleComputeTask();
	}

	protected class SingleComputeTask extends ComputeTask.SingleComputeTask {
		final static int SEED = 113;
		
		public final double num_flops(int Num_samples) {
			// 3 flops in x^2+y^2 and 1 flop in random routine

			return ((double) Num_samples)* 4.0;

		}

		public final double integrate(int numSamples) {

			Random R = new Random(SEED);

			int underCurve = 0;
			for (int count = 0; count < numSamples; count++) {

				double x = R.nextDouble();
				double y = R.nextDouble();

				if ( x*x + y*y <= 1.0) {
					underCurve ++;
				}
			}
			return ((double) underCurve / numSamples) * 4.0;
		}

		public double measureMonteCarlo(double min_time, Random R) {
			Stopwatch Q = new Stopwatch();

			// Cycles set to integrate into SPECjvm2008 benchmark harness.  Testing done on
			// Apple Macbook Pro 2.0Ghz Intel Core Duo, 1GB 667mhz SODIMM
			// J2SE 5.0_06 (Apple)
			// Tuning: -server
			int cycles=16777216;
			double x =0.0;

			Q.start();
			x = integrate(cycles);
			Q.stop();

			return num_flops(cycles) / Q.read() * 1.0e-6;
		}
		
		public void realRun() {
			double min_time = Constants.RESOLUTION_DEFAULT;

			// run the benchmark

			double res = 0.0;
			Random R = new Random(Constants.RANDOM_SEED);
			res = measureMonteCarlo(min_time, R);
		}
	}
	
	public static void main(String [] args) {
		if (args.length < 4) {
			System.err.println("Arguments: [adaptor_name] [config_file] [num_threads] [num_runs]");
			System.exit(1);
		}
		Adaptor adaptor = Adaptor.getAdaptorByName(args[0]);
		if (adaptor == null) {
			System.err.println("Unknown adaptor");
			System.exit(1);
		}
		
		adaptor.loadConfigFromFile(args[1]);		
		ScimarkMonteCarloTask task = new ScimarkMonteCarloTask(adaptor);
		task.configs.put("num_threads", args[2]);
		task.configs.put("num_runs", args[3]);
		Map<String, String> results = task.run();
		ComputeTask.printResults(results);
	}
}



