import java.io.PrintWriter;

public class StackFrames {
	private final int MAX_FRAMES = 128;
	private int top = -1;
	private int oldTop = -1;
	
	private final Graph[] frames = new Graph[MAX_FRAMES];
	
	public StackFrames(){
		for(int i = 0; i < frames.length; i++){
			frames[i] = null;
		}
	}
	
	public StackFrames(StackFrames other){
		for(int i = 0; i < frames.length; i++){
			frames[i] = other.getFrame(i);
		}
		top = oldTop = other.getTop();
	}

	public Graph getFrame(int i) {
		return frames[i];
	}
	
	public boolean pushFrame(Graph graph) {
		if(top + 1 < MAX_FRAMES){
			top++;
			frames[top] = graph;
			return true;
		}
		return false;
	}
	
	public Graph popFrame() {
		if(top >= 0 && (oldTop == -1 || top > oldTop)){
			Graph graph = frames[top];
			frames[top] = null;
			top--;
			return graph;
		}
		return null;
	}
	
	public Graph getRandFrameNotTop(Distribution dist){
		return frames[dist.randUInt(top)];
	}
	
	public int getTop(){
		return top;
	}
	
	public Graph top(){
		if(top >= 0){
			return frames[top];
		} else {
			return null;
		}
	}

	public void empty() {
		while (top > oldTop){
			Graph graph = popFrame();
			if(graph == null){
				return;
			}
			graph.empty();
		}
	}
	
	public GraphNode getRandObjNotTop(Distribution dist){
		Graph graph = getRandFrameNotTop(dist);
		if(graph != null){
			return graph.getRandomObj(dist, false);
		}
		return null;
	}

	public void doRandAction(PrintWriter out, Distribution dist) {
		double rnd = dist.randU();
		
		int action = Settings.getRandAction(rnd);
		
		if(action == 4 || top() == null){
			addTopFrame(dist);			
		} else if(action == 5){
			removeTopFrame();
		} else {
			top().doAction(action, dist);
		}
	}
	
	public void addTopFrame(Distribution dist){
		//New Stack Frame
		Graph prevTop = top();
		pushFrame(new Graph());
		if(prevTop != null){
			for(int i = 0; i < Settings.getIntProperty("NEW_FRAME_ADDS"); i++){
				top().add(dist, prevTop);
			}
		}
	}
	
	public void removeTopFrame(){
		//Remove Stack Frame
		Graph graph = popFrame();
		if(graph != null){
			graph.empty();
		}
	}

	public void traverse() {
		for(int i = 0; i < top; i++){
			frames[i].traverse();
		}
	}
}
