
public class MyRun implements Runnable
{

	int i=100;
	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		while(i>0)
		{
			synchronized (this)
			{
				System.out.println(Thread.currentThread().getName()+"   "+i);
			
				i--;
				
			}
			
		}
	}

}
