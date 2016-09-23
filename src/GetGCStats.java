

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GetGCStats
 */
@WebServlet("/GetGCStats")
public class GetGCStats extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetGCStats() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long totalGarbageCollections = 0;
	    long garbageCollectionTime = 0;

	    for(GarbageCollectorMXBean gc :
	            ManagementFactory.getGarbageCollectorMXBeans()) {

	        long count = gc.getCollectionCount();

	        if(count >= 0) {
	            totalGarbageCollections += count;
	        }

	        long time = gc.getCollectionTime();

	        if(time >= 0) {
	            garbageCollectionTime += time;
	        }
	    }
	    response.getWriter().append("" + totalGarbageCollections+" ");
	    response.getWriter().append("" + garbageCollectionTime+"\n");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
