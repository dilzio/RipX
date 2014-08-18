package org.dilzio.riphttp.core;

import org.dilzio.riphttp.util.Pair;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConcatTest {

	//@Test
	public void happy() {
	    int ITERS = 100000000;

        long start1 = System.currentTimeMillis();
        for (int i = 0; i < ITERS; i++){
            String s = "this is my string: " + i;
        }
        long end1 = System.currentTimeMillis();
        System.out.println("Concat took: " + (end1 - start1) + " ms.");


        long start2 = System.currentTimeMillis();
        for (int i = 0; i < ITERS; i++){
           StringBuffer sb = new StringBuffer();
           sb.append("This is my str ");
           sb.append(i);
           sb.toString();
        }
        long end2 = System.currentTimeMillis();
        System.out.println("SB took: " + (end2 - start2) + " ms.");

        long start3 = System.currentTimeMillis();
        for (int i = 0; i < ITERS; i++){
            String.format("This is my %s string", i);
        }
        long end3 = System.currentTimeMillis();
        System.out.println("Format took: " + (end3 - start3) + " ms.");
    }

}
