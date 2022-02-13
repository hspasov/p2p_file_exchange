package bg.sofia.uni.fmi.mjt.torrent;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class Main {

    public static void main(String[] args) {
	// write your code here
        Future<String> f = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return null;
            }
        });
        System.out.println(f);
    }
}
