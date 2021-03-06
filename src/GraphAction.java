/*
Copyright 2016 the project authors as listed in the AUTHORS file.
All rights reserved. Use of this source code is governed by the
license that can be found in the LICENSE file.
*/

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GraphAction
 */
@WebServlet("/GraphAction")
public class GraphAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public static final StackFrames globalStack = new StackFrames();
	public static volatile int outOfMemErrs = 0;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GraphAction() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		StackFrames localStack = new StackFrames(globalStack);
		Distribution dist = new Distribution();
		
		for(int i = 0; i < Settings.getIntProperty("ACTIONS_PER_REQUEST"); i++){
			try {
				localStack.doRandAction(response.getWriter(), dist);
			} catch (OutOfMemoryError e){
				outOfMemErrs++;
				System.err.println("Out of Memory " + outOfMemErrs +" times!");
				System.err.println("Removing top frame of local stack!");
				localStack.removeTopFrame();
				if(outOfMemErrs>1){
					System.err.println("Removing top frame of global stack!");
					globalStack.removeTopFrame();
				}
				
			}
		}
		localStack.empty();
		Runtime runtime = Runtime.getRuntime();
		response.getWriter().append("" + (System.currentTimeMillis())+"\n");
		response.getWriter().append("" + (runtime.totalMemory() - runtime.freeMemory())+"\n");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
