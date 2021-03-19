package com.quasarbyte.aparapi.aparapi_hello_world;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class MainCPUStreamParallel {

    public final static int ARRAY_SIZE = 1024 * 1024 * 500;

    public final static int ARRAY_PART_COUNT = 4;

    public static void main(String[] args) {

        final float[] inA = new float[ARRAY_SIZE];
        final float[] inB = new float[ARRAY_SIZE];

        for (int i = 0; i < inA.length; i++) {
            inA[i] = 1;
            inB[i] = 1;
        }

        final float[] result = new float[inA.length];

        LocalDateTime localDateTimeBegin = LocalDateTime.now();

        IntStream.range(0, result.length)
                .parallel()
                .forEach(
                        i -> {
                            result[i] = -(inA[i] + inB[i] + (float) Math.exp(1.0) * (float) Math.exp(1.0)) / inA[i];
                        }
                );

        LocalDateTime localDateTimeEnd = LocalDateTime.now();

        Duration duration = Duration.between(localDateTimeBegin, localDateTimeEnd);

        System.out.println(
                String.format(
                        "Time spent: Hours: %d, Minutes: %d, Seconds: %d, Milliseconds: %d",
                        duration.toHoursPart(),
                        duration.toMinutesPart(),
                        duration.toSecondsPart(),
                        duration.toMillisPart())
        );

    }

    public static int getStartPositionByPartNumber(int partNumber) {

        int partSize = ARRAY_SIZE / ARRAY_PART_COUNT;

        int startPosition = partSize * partNumber;

        return startPosition;
    }

    public static int getEndPositionByPartNumber(int partNumber) {

        int partSize = ARRAY_SIZE / ARRAY_PART_COUNT;

        int startPosition = partSize * partNumber;

        int endPosition = startPosition + partSize - 1 + (partNumber + 1 == ARRAY_PART_COUNT ? ARRAY_SIZE % partSize : 0);

        return endPosition;
    }

    public static Callable<Object> getCallable(int partNumber, Consumer<Integer> consumer) {
        return Executors.callable(
                new Runnable() {
                    @Override
                    public void run() {

                        int startPosition = getStartPositionByPartNumber(partNumber);
                        int endPosition = getEndPositionByPartNumber(partNumber);

                        for (int i = startPosition; i <= endPosition; i++) {
                            consumer.accept(i);
                        }

                    }
                }
        );
    }

}
