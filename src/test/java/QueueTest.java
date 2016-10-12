import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class QueueTest
{
	public static void main(String[] args)
	{
		QueueTest test=new QueueTest();
		ArrayBlockingQueue queue=new ArrayBlockingQueue<String>(1000);
		 
		Producer p1=test.new Producer(queue);
		
		new Thread(p1).start();
		Consumer c1=test.new Consumer(queue);
		new Thread(c1).start();
		new Thread(c1).start();

		new Thread(c1).start();
	}
	public class Producer implements Runnable
	{
		private BlockingQueue queue;
		private int i=0;
		public Producer(ArrayBlockingQueue queue)
		{
			this.queue=queue;
		}
		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			while(true)
			{
				i++;
				queue.add("producer "+i);
				System.out.println("add producer"+i);
				if(i>100)
					return;
			}
		}
	}
	public class Consumer implements Runnable
	{
		private BlockingQueue queue;
		public Consumer(ArrayBlockingQueue queue)
		{
			this.queue=queue;
		}
		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			while(true)
			{
				try
				{
					System.out.println("take "+queue.take().toString());
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
//	public class Producer extends Thread
//	{
//		private int i;
//		private String threadName;
//		public Producer(String threadName,int i)
//		{
//			this.threadName=threadName;
//			this.i=i;
//		}
//		@Override
//		public void run()
//		{
//			// TODO Auto-generated method stub
//			super.run();
//			for(int a=0;a<i;a++)
//			{
//				if(a>10)
//				{
//					return;
//				}
//				System.out.println("this is "+threadName+"  "+a+"ÔËÐÐ");
//				try
//				{
//					Thread.sleep(1000);
//				}
//				catch (InterruptedException e)
//				{
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//			}
//		}
		
		
		
	//}
}


