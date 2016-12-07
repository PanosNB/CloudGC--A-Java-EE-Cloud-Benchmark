

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class startAVS
 */
@WebServlet("/StartAVS")
public class StartAVS extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static volatile Thread bgThread = null;
	
	public static String outPath;  
	
	public static boolean noChange = false;
	public static boolean noGC = false;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StartAVS() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected synchronized void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		if(request.getParameter("noChange")!=null){
			if(request.getParameter("noChange").equals("1")){
				noChange = true;
			}
		}
		
		if(request.getParameter("noGC")!=null){
			if(request.getParameter("noGC").equals("1")){
				noGC = true;
			}
		}
		
		if (bgThread == null){
			bgThread = new Thread(new SamplerTask());
			outPath = "apps/myapp.war/";
		}
		
		if (!bgThread.isAlive()){
			
			bgThread.start();
			
			response.getOutputStream().println("Started!");
		} else {
			response.getOutputStream().println("Already started!");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
