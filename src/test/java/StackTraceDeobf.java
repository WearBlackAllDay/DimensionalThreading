import java.io.*;
import java.util.regex.Pattern;

public class StackTraceDeobf {

	public static String MAPPINGS = "";

	public static void main(String[] args) throws IOException {
		String stackTrace =
				"[17:02:54] [dimthread_overworld/ERROR]: Failed to save chunk 22,71\n" +
				"java.lang.NullPointerException: null\n" +
				"    at net.minecraft.class_2852.method_12385(class_2852.java:413) ~[intermediary-server.jar:?]\n" +
				"    at net.minecraft.class_2852.method_12410(class_2852.java:359) ~[intermediary-server.jar:?]\n" +
				"    at net.minecraft.class_3898.method_17228(class_3898.java:716) ~[intermediary-server.jar:?]\n" +
				"    at net.minecraft.class_3898.method_18843(class_3898.java:443) ~[intermediary-server.jar:?]\n" +
				"    at java.util.concurrent.CompletableFuture.uniAccept(CompletableFuture.java:670) ~[?:1.8.0_252]\n" +
				"    at java.util.concurrent.CompletableFuture$UniAccept.tryFire(CompletableFuture.java:646) ~[?:1.8.0_252]\n" +
				"    at java.util.concurrent.CompletableFuture$Completion.run(CompletableFuture.java:456) ~[?:1.8.0_252]\n" +
				"    at net.minecraft.class_3898.method_20605(class_3898.java:424) ~[intermediary-server.jar:?]\n" +
				"    at net.minecraft.class_3898.method_17233(class_3898.java:401) ~[intermediary-server.jar:?]\n" +
				"    at net.minecraft.class_3215.method_12127(class_3215.java:339) ~[intermediary-server.jar:?]\n" +
				"    at net.minecraft.class_3218.method_18765(class_3218.java:348) ~[intermediary-server.jar:?]\n" +
				"    at net.minecraft.server.MinecraftServer.mdd721e6$lambda$null$0$2(MinecraftServer.java:3240) ~[intermediary-server.jar:?]\n" +
				"    at dimthread.DimThread.swapThreadsAndRun(DimThread.java:19) ~[DimThread-1.0-SNAPSHOT.jar:?]\n" +
				"    at net.minecraft.server.MinecraftServer.mdd721e6$lambda$tickWorlds$1$1(MinecraftServer.java:3240) ~[intermediary-server.jar:?]\n" +
				"    at dimthread.thread.ThreadPool.lambda$null$3(ThreadPool.java:43) ~[DimThread-1.0-SNAPSHOT.jar:?]\n" +
				"    at dimthread.thread.ThreadPool.lambda$run$0(ThreadPool.java:33) ~[DimThread-1.0-SNAPSHOT.jar:?]\n" +
				"    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149) [?:1.8.0_252]\n" +
				"    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624) [?:1.8.0_252]\n" +
				"    at java.lang.Thread.run(Thread.java:748) [?:1.8.0_252]";

		System.out.println(StackTraceDeobf.deobfuscate(stackTrace));
	}

	private static String deobfuscate(String stackTrace) throws IOException {
		if(MAPPINGS.isEmpty()) {
			File file = new File("src/test/java/mappings.tiny");
			System.out.println(file.getAbsolutePath());
			BufferedReader reader = new BufferedReader(new FileReader(file));
			StringBuilder sb = new StringBuilder();
			while(reader.ready())sb.append(reader.readLine()).append("|");
			MAPPINGS = sb.toString();
		}

		while(true) {
			String value = next(stackTrace);
			if(value == null)return stackTrace;
			stackTrace = value;
		}
	}

	private static String next(String stackTrace) {
		String newS;

		newS = findAndMap(stackTrace, "class");
		if(newS != null)return newS;

		newS = findAndMap(stackTrace, "method");
		if(newS != null)return newS;

		newS = findAndMap(stackTrace, "field");
		if(newS != null)return newS;

		return null;
	}

	public static String findAndMap(String stackTrace, String prefix) {
		int split = stackTrace.indexOf(prefix + "_");
		if(split == -1)return null;

		String a = stackTrace.substring(0, split);
		String b = stackTrace.substring(split + prefix.length() + 1);

		StringBuilder numString = new StringBuilder();

		for(int i = 0; true; i++) {
			char c = b.charAt(i);
			if(!Character.isDigit(c))break;
			numString.append(c);
		}

		b = b.substring(numString.length());
		String entry = prefix + "_" + numString;
		String mapping = getMapping(entry);

		if(mapping.equals(entry)) {
			mapping = prefix + "<UNMAPPED>_" + numString;
		}

		return a + mapping + b;
	}

	private static String getMapping(String raw) {
		int id = MAPPINGS.indexOf(raw);

		if(id == -1) {
			System.err.println("Unknown entry [" + raw + "]!");
			return raw;
		}

		String[] arr = MAPPINGS
				.split(Pattern.quote(raw), 2)[1]
				.split(Pattern.quote("|"))[0]
				.split(Pattern.quote("/"));
		return arr[arr.length - 1].trim();
	}

}
