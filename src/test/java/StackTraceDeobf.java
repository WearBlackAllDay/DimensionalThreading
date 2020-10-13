import java.io.*;
import java.util.regex.Pattern;

public class StackTraceDeobf {

	public static String MAPPINGS = "";

	
	public static void main(String[] args) throws IOException {
		String stackTrace =
				"java.lang.Error\n" +
						"\tat java.lang.Object.wait(Native Method)\n" +
						"\tat java.lang.Object.wait(Object.java:502)\n" +
						"\tat dimthread.thread.IntLatch.waitUntil(IntLatch.java:42)\n" +
						"\tat dimthread.thread.ThreadPool.waitUntil(ThreadPool.java:56)\n" +
						"\tat dimthread.thread.ThreadPool.awaitCompletion(ThreadPool.java:51)\n" +
						"\tat net.minecraft.server.MinecraftServer.handler$zgj000$tickWorlds(MinecraftServer.java:3256)\n" +
						"\tat net.minecraft.server.MinecraftServer.method_3813(MinecraftServer.java:857)\n" +
						"\tat net.minecraft.class_3176.method_3813(class_3176.java:303)\n" +
						"\tat net.minecraft.server.MinecraftServer.method_3748(MinecraftServer.java:808)\n" +
						"\tat net.minecraft.server.MinecraftServer.handler$zba000$modifiedRunLoop(MinecraftServer.java:2297)\n" +
						"\tat net.minecraft.server.MinecraftServer.method_29741(MinecraftServer.java:648)\n" +
						"\tat net.minecraft.server.MinecraftServer.method_29739(MinecraftServer.java:254)\n" +
						"\tat net.minecraft.server.MinecraftServer$$Lambda$3874/460034654.run(Unknown Source)\n" +
						"\tat java.lang.Thread.run(Thread.java:748)\n" +
						"\n" +
						"\n" +
						"A detailed walkthrough of the error, its code path and all known details is as follows:\n" +
						"---------------------------------------------------------------------------------------\n" +
						"\n" +
						"-- Head --\n" +
						"Thread: Server Watchdog\n" +
						"Stacktrace:\n" +
						"\tat java.lang.Object.wait(Native Method)\n" +
						"\tat java.lang.Object.wait(Object.java:502)\n" +
						"\tat dimthread.thread.IntLatch.waitUntil(IntLatch.java:42)\n" +
						"\tat dimthread.thread.ThreadPool.waitUntil(ThreadPool.java:56)\n" +
						"\tat dimthread.thread.ThreadPool.awaitCompletion(ThreadPool.java:51)\n" +
						"\tat net.minecraft.server.MinecraftServer.handler$zgj000$tickWorlds(MinecraftServer.java:3256)\n" +
						"\tat net.minecraft.server.MinecraftServer.method_3813(MinecraftServer.java:857)\n" +
						"\tat net.minecraft.class_3176.method_3813(class_3176.java:303)\n" +
						"\tat net.minecraft.server.MinecraftServer.method_3748(MinecraftServer.java:808)\n" +
						"\tat net.minecraft.server.MinecraftServer.handler$zba000$modifiedRunLoop(MinecraftServer.java:2297)\n" +
						"\tat net.minecraft.server.MinecraftServer.method_29741(MinecraftServer.java:648)\n" +
						"\tat net.minecraft.server.MinecraftServer.method_29739(MinecraftServer.java:254)\n" +
						"\n" +
						"-- Thread Dump --\n" +
						"Details:\n" +
						"\tThreads: \"IO-Worker-24\" Id=63 TIMED_WAITING on java.util.concurrent.SynchronousQueue$TransferStack@35aa58b2\n" +
						"\tat sun.misc.Unsafe.park(Native Method)\n" +
						"\t-  waiting on java.util.concurrent.SynchronousQueue$TransferStack@35aa58b2\n" +
						"\tat java.util.concurrent.locks.LockSupport.parkNanos(LockSupport.java:215)\n" +
						"\tat java.util.concurrent.SynchronousQueue$TransferStack.awaitFulfill(SynchronousQueue.java:460)\n" +
						"\tat java.util.concurrent.SynchronousQueue$TransferStack.transfer(SynchronousQueue.java:362)\n" +
						"\tat java.util.concurrent.SynchronousQueue.poll(SynchronousQueue.java:941)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1073)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)\n" +
						"\t...\n" +
						"\n" +
						"\n" +
						"\"IO-Worker-26\" Id=65 TIMED_WAITING on java.util.concurrent.SynchronousQueue$TransferStack@35aa58b2\n" +
						"\tat sun.misc.Unsafe.park(Native Method)\n" +
						"\t-  waiting on java.util.concurrent.SynchronousQueue$TransferStack@35aa58b2\n" +
						"\tat java.util.concurrent.locks.LockSupport.parkNanos(LockSupport.java:215)\n" +
						"\tat java.util.concurrent.SynchronousQueue$TransferStack.awaitFulfill(SynchronousQueue.java:460)\n" +
						"\tat java.util.concurrent.SynchronousQueue$TransferStack.transfer(SynchronousQueue.java:362)\n" +
						"\tat java.util.concurrent.SynchronousQueue.poll(SynchronousQueue.java:941)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1073)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)\n" +
						"\t...\n" +
						"\n" +
						"\n" +
						"\"IO-Worker-25\" Id=64 TIMED_WAITING on java.util.concurrent.SynchronousQueue$TransferStack@35aa58b2\n" +
						"\tat sun.misc.Unsafe.park(Native Method)\n" +
						"\t-  waiting on java.util.concurrent.SynchronousQueue$TransferStack@35aa58b2\n" +
						"\tat java.util.concurrent.locks.LockSupport.parkNanos(LockSupport.java:215)\n" +
						"\tat java.util.concurrent.SynchronousQueue$TransferStack.awaitFulfill(SynchronousQueue.java:460)\n" +
						"\tat java.util.concurrent.SynchronousQueue$TransferStack.transfer(SynchronousQueue.java:362)\n" +
						"\tat java.util.concurrent.SynchronousQueue.poll(SynchronousQueue.java:941)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1073)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)\n" +
						"\t...\n" +
						"\n" +
						"\n" +
						"\"IO-Worker-23\" Id=62 TIMED_WAITING on java.util.concurrent.SynchronousQueue$TransferStack@35aa58b2\n" +
						"\tat sun.misc.Unsafe.park(Native Method)\n" +
						"\t-  waiting on java.util.concurrent.SynchronousQueue$TransferStack@35aa58b2\n" +
						"\tat java.util.concurrent.locks.LockSupport.parkNanos(LockSupport.java:215)\n" +
						"\tat java.util.concurrent.SynchronousQueue$TransferStack.awaitFulfill(SynchronousQueue.java:460)\n" +
						"\tat java.util.concurrent.SynchronousQueue$TransferStack.transfer(SynchronousQueue.java:362)\n" +
						"\tat java.util.concurrent.SynchronousQueue.poll(SynchronousQueue.java:941)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1073)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)\n" +
						"\t...\n" +
						"\n" +
						"\n" +
						"\"IO-Worker-22\" Id=61 TIMED_WAITING on java.util.concurrent.SynchronousQueue$TransferStack@35aa58b2\n" +
						"\tat sun.misc.Unsafe.park(Native Method)\n" +
						"\t-  waiting on java.util.concurrent.SynchronousQueue$TransferStack@35aa58b2\n" +
						"\tat java.util.concurrent.locks.LockSupport.parkNanos(LockSupport.java:215)\n" +
						"\tat java.util.concurrent.SynchronousQueue$TransferStack.awaitFulfill(SynchronousQueue.java:460)\n" +
						"\tat java.util.concurrent.SynchronousQueue$TransferStack.transfer(SynchronousQueue.java:362)\n" +
						"\tat java.util.concurrent.SynchronousQueue.poll(SynchronousQueue.java:941)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1073)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)\n" +
						"\t...\n" +
						"\n" +
						"\n" +
						"\"IO-Worker-21\" Id=60 TIMED_WAITING on java.util.concurrent.SynchronousQueue$TransferStack@35aa58b2\n" +
						"\tat sun.misc.Unsafe.park(Native Method)\n" +
						"\t-  waiting on java.util.concurrent.SynchronousQueue$TransferStack@35aa58b2\n" +
						"\tat java.util.concurrent.locks.LockSupport.parkNanos(LockSupport.java:215)\n" +
						"\tat java.util.concurrent.SynchronousQueue$TransferStack.awaitFulfill(SynchronousQueue.java:460)\n" +
						"\tat java.util.concurrent.SynchronousQueue$TransferStack.transfer(SynchronousQueue.java:362)\n" +
						"\tat java.util.concurrent.SynchronousQueue.poll(SynchronousQueue.java:941)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1073)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)\n" +
						"\t...\n" +
						"\n" +
						"\n" +
						"\"dimthread_koth/game3\" Id=59 RUNNABLE\n" +
						"\tat net.minecraft.server.MinecraftServer.md0b6654$lambda$null$0$2(MinecraftServer.java:3247)\n" +
						"\tat net.minecraft.server.MinecraftServer$$Lambda$4182/45414315.run(Unknown Source)\n" +
						"\tat dimthread.DimThread.swapThreadsAndRun(DimThread.java:19)\n" +
						"\tat net.minecraft.server.MinecraftServer.md0b6654$lambda$tickWorlds$1$1(MinecraftServer.java:3242)\n" +
						"\tat net.minecraft.server.MinecraftServer$$Lambda$4178/1551995194.accept(Unknown Source)\n" +
						"\tat dimthread.thread.ThreadPool.lambda$null$3(ThreadPool.java:43)\n" +
						"\tat dimthread.thread.ThreadPool$$Lambda$4180/1765806307.run(Unknown Source)\n" +
						"\tat dimthread.thread.ThreadPool.lambda$run$0(ThreadPool.java:33)\n" +
						"\t...\n" +
						"\n" +
						"\tNumber of locked synchronizers = 1\n" +
						"\t- java.util.concurrent.ThreadPoolExecutor$Worker@25aada3b\n" +
						"\n" +
						"\n" +
						"\"dimthread_koth/game2\" Id=58 RUNNABLE\n" +
						"\tat net.minecraft.class_3204$class_3205.method_14057(class_3204.java:298)\n" +
						"\tat net.minecraft.class_3204.method_15892(class_3204.java:105)\n" +
						"\tat net.minecraft.class_3215.method_16155(class_3215.java:284)\n" +
						"\tat net.minecraft.class_3215.method_19490(class_3215.java:48)\n" +
						"\tat net.minecraft.class_3215$class_4212.method_16075(class_3215.java:550)\n" +
						"\tat net.minecraft.class_3215.method_19492(class_3215.java:280)\n" +
						"\tat net.minecraft.server.MinecraftServer.md0b6654$lambda$null$0$2(MinecraftServer.java:3247)\n" +
						"\tat net.minecraft.server.MinecraftServer$$Lambda$4182/45414315.run(Unknown Source)\n" +
						"\t...\n" +
						"\n" +
						"\tNumber of locked synchronizers = 1\n" +
						"\t- java.util.concurrent.ThreadPoolExecutor$Worker@4c840c40\n" +
						"\n" +
						"\n" +
						"\"dimthread_koth/game1\" Id=57 RUNNABLE\n" +
						"\tat net.minecraft.server.MinecraftServer.md0b6654$lambda$null$0$2(MinecraftServer.java:3247)\n" +
						"\tat net.minecraft.server.MinecraftServer$$Lambda$4182/45414315.run(Unknown Source)\n" +
						"\tat dimthread.DimThread.swapThreadsAndRun(DimThread.java:19)\n" +
						"\tat net.minecraft.server.MinecraftServer.md0b6654$lambda$tickWorlds$1$1(MinecraftServer.java:3242)\n" +
						"\tat net.minecraft.server.MinecraftServer$$Lambda$4178/1551995194.accept(Unknown Source)\n" +
						"\tat dimthread.thread.ThreadPool.lambda$null$3(ThreadPool.java:43)\n" +
						"\tat dimthread.thread.ThreadPool$$Lambda$4180/1765806307.run(Unknown Source)\n" +
						"\tat dimthread.thread.ThreadPool.lambda$run$0(ThreadPool.java:33)\n" +
						"\t...\n" +
						"\n" +
						"\tNumber of locked synchronizers = 1\n" +
						"\t- java.util.concurrent.ThreadPoolExecutor$Worker@1a0271cc\n" +
						"\n" +
						"\n" +
						"\"Java2D Disposer\" Id=55 WAITING on java.lang.ref.ReferenceQueue$Lock@3b6db077\n" +
						"\tat java.lang.Object.wait(Native Method)\n" +
						"\t-  waiting on java.lang.ref.ReferenceQueue$Lock@3b6db077\n" +
						"\tat java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:144)\n" +
						"\tat java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:165)\n" +
						"\tat sun.java2d.Disposer.run(Disposer.java:148)\n" +
						"\tat java.lang.Thread.run(Thread.java:748)\n" +
						"\n" +
						"\n" +
						"\"Server Watchdog\" Id=54 RUNNABLE\n" +
						"\tat sun.management.ThreadImpl.dumpThreads0(Native Method)\n" +
						"\tat sun.management.ThreadImpl.dumpAllThreads(ThreadImpl.java:454)\n" +
						"\tat net.minecraft.class_3178.run(class_3178.java:45)\n" +
						"\tat java.lang.Thread.run(Thread.java:748)\n" +
						"\n" +
						"\n" +
						"\"Worker-Main-20\" Id=53 WAITING on java.util.concurrent.ForkJoinPool@3f5233c2\n" +
						"\tat sun.misc.Unsafe.park(Native Method)\n" +
						"\t-  waiting on java.util.concurrent.ForkJoinPool@3f5233c2\n" +
						"\tat java.util.concurrent.ForkJoinPool.awaitWork(ForkJoinPool.java:1824)\n" +
						"\tat java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1693)\n" +
						"\tat java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)\n" +
						"\n" +
						"\n" +
						"\"IO-Worker-19\" Id=52 TIMED_WAITING on java.util.concurrent.SynchronousQueue$TransferStack@35aa58b2\n" +
						"\tat sun.misc.Unsafe.park(Native Method)\n" +
						"\t-  waiting on java.util.concurrent.SynchronousQueue$TransferStack@35aa58b2\n" +
						"\tat java.util.concurrent.locks.LockSupport.parkNanos(LockSupport.java:215)\n" +
						"\tat java.util.concurrent.SynchronousQueue$TransferStack.awaitFulfill(SynchronousQueue.java:460)\n" +
						"\tat java.util.concurrent.SynchronousQueue$TransferStack.transfer(SynchronousQueue.java:362)\n" +
						"\tat java.util.concurrent.SynchronousQueue.poll(SynchronousQueue.java:941)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1073)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)\n" +
						"\t...\n" +
						"\n" +
						"\n" +
						"\"IO-Worker-18\" Id=51 TIMED_WAITING on java.util.concurrent.SynchronousQueue$TransferStack@35aa58b2\n" +
						"\tat sun.misc.Unsafe.park(Native Method)\n" +
						"\t-  waiting on java.util.concurrent.SynchronousQueue$TransferStack@35aa58b2\n" +
						"\tat java.util.concurrent.locks.LockSupport.parkNanos(LockSupport.java:215)\n" +
						"\tat java.util.concurrent.SynchronousQueue$TransferStack.awaitFulfill(SynchronousQueue.java:460)\n" +
						"\tat java.util.concurrent.SynchronousQueue$TransferStack.transfer(SynchronousQueue.java:362)\n" +
						"\tat java.util.concurrent.SynchronousQueue.poll(SynchronousQueue.java:941)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1073)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)\n" +
						"\t...\n" +
						"\n" +
						"\n" +
						"\"IO-Worker-17\" Id=50 TIMED_WAITING on java.util.concurrent.SynchronousQueue$TransferStack@35aa58b2\n" +
						"\tat sun.misc.Unsafe.park(Native Method)\n" +
						"\t-  waiting on java.util.concurrent.SynchronousQueue$TransferStack@35aa58b2\n" +
						"\tat java.util.concurrent.locks.LockSupport.parkNanos(LockSupport.java:215)\n" +
						"\tat java.util.concurrent.SynchronousQueue$TransferStack.awaitFulfill(SynchronousQueue.java:460)\n" +
						"\tat java.util.concurrent.SynchronousQueue$TransferStack.transfer(SynchronousQueue.java:362)\n" +
						"\tat java.util.concurrent.SynchronousQueue.poll(SynchronousQueue.java:941)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1073)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)\n" +
						"\t...\n" +
						"\n" +
						"\n" +
						"\"IO-Worker-16\" Id=49 TIMED_WAITING on java.util.concurrent.SynchronousQueue$TransferStack@35aa58b2\n" +
						"\tat sun.misc.Unsafe.park(Native Method)\n" +
						"\t-  waiting on java.util.concurrent.SynchronousQueue$TransferStack@35aa58b2\n" +
						"\tat java.util.concurrent.locks.LockSupport.parkNanos(LockSupport.java:215)\n" +
						"\tat java.util.concurrent.SynchronousQueue$TransferStack.awaitFulfill(SynchronousQueue.java:460)\n" +
						"\tat java.util.concurrent.SynchronousQueue$TransferStack.transfer(SynchronousQueue.java:362)\n" +
						"\tat java.util.concurrent.SynchronousQueue.poll(SynchronousQueue.java:941)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1073)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)\n" +
						"\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)\n" +
						"\t...\n" +
						"\n" +
						"\n" +
						"\"Netty Epoll Server IO #0\" Id=47 RUNNABLE (in native)\n" +
						"\tat io.netty.channel.epoll.Native.epollWait0(Native Method)\n" +
						"\tat io.netty.channel.epoll.Native.epollWait(Native.java:114)\n" +
						"\tat io.netty.channel.epoll.EpollEventLoop.epollWait(EpollEventLoop.java:239)\n" +
						"\tat io.netty.channel.epoll.EpollEventLoop.run(EpollEventLoop.java:256)\n" +
						"\tat io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:884)\n" +
						"\tat java.lang.Thread.run(Thread.java:748)\n" +
						"\n" +
						"\n" +
						"\"ObjectCleanerThread\" Id=46 TIMED_WAITING on java.lang.ref.ReferenceQueue$Lock@7a50ba4c\n" +
						"\tat java.lang.Object.wait(Native Method)\n" +
						"\t-  waiting on java.lang.ref.ReferenceQueue$Lock@7a50ba4c\n" +
						"\tat java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:144)\n" +
						"\tat io.netty.util.internal.ObjectCleaner$1.run(ObjectCleaner.java:54)\n" +
						"\tat io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)\n" +
						"\tat java.lang.Thread.run(Thread.java:748)\n" +
						"\n" +
						"\n" +
						"\"Server console handler\" Id=45 RUNNABLE (in native)\n" +
						"\tat java.io.FileInputStream.readBytes(Native Method)\n" +
						"\tat java.io.FileInputStream.read(FileInputStream.java:255)\n" +
						"\tat java.io.BufferedInputStream.read1(BufferedInputStream.java:284)\n" +
						"\tat java.io.BufferedInputStream.read(BufferedInputStream.java:345)\n" +
						"\t-  locked java.io.BufferedInputStream@1dd8127c\n" +
						"\tat sun.nio.cs.StreamDecoder.readBytes(StreamDecoder.java:284)\n" +
						"\tat sun.nio.cs.StreamDecoder.implRead(StreamDecoder.java:326)\n" +
						"\tat sun.nio.cs.StreamDecoder.read(StreamDecoder.java:178)\n" +
						"\t-  locked java.io.InputStreamReader@4b60e7b8\n" +
						"\tat java.io.InputStreamReader.read(InputStreamReader.java:184)\n" +
						"\t...\n" +
						"\n" +
						"\n" +
						"\"DestroyJavaVM\" Id=44 RUNNABLE\n" +
						"\n" +
						"\n" +
						"\"Server thread\" Id=41 WAITING on java.lang.Object@65021135\n" +
						"\tat java.lang.Object.wait(Native Method)\n" +
						"\t-  waiting on java.lang.Object@65021135\n" +
						"\tat java.lang.Object.wait(Object.java:502)\n" +
						"\tat dimthread.thread.IntLatch.waitUntil(IntLatch.java:42)\n" +
						"\tat dimthread.thread.ThreadPool.waitUntil(ThreadPool.java:56)\n" +
						"\tat dimthread.thread.ThreadPool.awaitCompletion(ThreadPool.java:51)\n" +
						"\tat net.minecraft.server.MinecraftServer.handler$zgj000$tickWorlds(MinecraftServer.java:3256)\n" +
						"\tat net.minecraft.server.MinecraftServer.method_3813(MinecraftServer.java:857)\n" +
						"\tat net.minecraft.class_3176.method_3813(class_3176.java:303)\n" +
						"\t...\n" +
						"\n" +
						"\n" +
						"\"Snooper Timer\" Id=42 WAITING on java.util.TaskQueue@1a7f4503\n" +
						"\tat java.lang.Object.wait(Native Method)\n" +
						"\t-  waiting on java.util.TaskQueue@1a7f4503\n" +
						"\tat java.lang.Object.wait(Object.java:502)\n" +
						"\tat java.util.TimerThread.mainLoop(Timer.java:526)\n" +
						"\tat java.util.TimerThread.run(Timer.java:505)\n" +
						"\n" +
						"\n" +
						"\"Worker-Main-14\" Id=40 TIMED_WAITING on java.util.concurrent.ForkJoinPool@3f5233c2\n" +
						"\tat sun.misc.Unsafe.park(Native Method)\n" +
						"\t-  waiting on java.util.concurrent.ForkJoinPool@3f5233c2\n" +
						"\tat java.util.concurrent.ForkJoinPool.awaitWork(ForkJoinPool.java:1824)\n" +
						"\tat java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1693)\n" +
						"\tat java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)\n" +
						"\n" +
						"\n" +
						"\"Timer hack thread\" Id=33 TIMED_WAITING\n" +
						"\tat java.lang.Thread.sleep(Native Method)\n" +
						"\tat net.minecraft.class_156$6.run(class_156.java:636)\n" +
						"\n" +
						"\n" +
						"\"Worker-Bootstrap-7\" Id=32 TIMED_WAITING on java.util.concurrent.ForkJoinPool@4769efc2\n" +
						"\tat sun.misc.Unsafe.park(Native Method)\n" +
						"\t-  waiting on java.util.concurrent.ForkJoinPool@4769efc2\n" +
						"\tat java.util.concurrent.ForkJoinPool.awaitWork(ForkJoinPool.java:1824)\n" +
						"\tat java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1693)\n" +
						"\tat java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)\n" +
						"\n" +
						"\n" +
						"\"Worker-Bootstrap-5\" Id=30 WAITING on java.util.concurrent.ForkJoinPool@4769efc2\n" +
						"\tat sun.misc.Unsafe.park(Native Method)\n" +
						"\t-  waiting on java.util.concurrent.ForkJoinPool@4769efc2\n" +
						"\tat java.util.concurrent.ForkJoinPool.awaitWork(ForkJoinPool.java:1824)\n" +
						"\tat java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1693)\n" +
						"\tat java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)\n" +
						"\n" +
						"\n" +
						"\"Signal Dispatcher\" Id=4 RUNNABLE\n" +
						"\n" +
						"\n" +
						"\"Finalizer\" Id=3 WAITING on java.lang.ref.ReferenceQueue$Lock@bf88db0\n" +
						"\tat java.lang.Object.wait(Native Method)\n" +
						"\t-  waiting on java.lang.ref.ReferenceQueue$Lock@bf88db0\n" +
						"\tat java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:144)\n" +
						"\tat java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:165)\n" +
						"\tat java.lang.ref.Finalizer$FinalizerThread.run(Finalizer.java:216)\n" +
						"\n" +
						"\n" +
						"\"Reference Handler\" Id=2 WAITING on java.lang.ref.Reference$Lock@7e06be43\n" +
						"\tat java.lang.Object.wait(Native Method)\n" +
						"\t-  waiting on java.lang.ref.Reference$Lock@7e06be43\n" +
						"\tat java.lang.Object.wait(Object.java:502)\n" +
						"\tat java.lang.ref.Reference.tryHandlePending(Reference.java:191)\n" +
						"\tat java.lang.ref.Reference$ReferenceHandler.run(Reference.java:153)\n" +
						"\n" +
						"\n" +
						"\n" +
						"Stacktrace:\n" +
						"\tat net.minecraft.class_3178.run(class_3178.java:61)\n" +
						"\tat java.lang.Thread.run(Thread.java:748)\n";

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
