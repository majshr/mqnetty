package schedule;

import java.util.Random;

public class Test {
    public static void main(String[] args) {
        RingBufferWheel wheel = new RingBufferWheel(4, 2);

        for (int i = 0; i < 100; i++) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    int val = new Random().nextInt(10);
                    wheel.addTask(new Runnable() {

                        @Override
                        public void run() {
                            System.out.println("hello world::: " + val);
                        }
                    }, val);
                }
            }).start();
        }

    }
}
