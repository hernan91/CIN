import org.moeaframework.util.progress.ProgressEvent;
import org.moeaframework.util.progress.ProgressListener;

public class ThreadProgress implements ProgressListener{
	public int threadNum;
	
	public ThreadProgress(int threadNum) {
		this.threadNum = threadNum;
	}
	
	public Float getProgress(ProgressEvent event) {
		return (float) (event.getCurrentSeed()/event.getTotalSeeds());
	}
	
	@Override
	public void progressUpdate(ProgressEvent event) {
		//System.out.println("Thread nro "+threadNum+" "+(float)getProgress(event)+"% completado.");
		System.out.println("Thread nro "+threadNum+" "+(int)(event.getPercentComplete()*100)+"% completado.");
	}

}
