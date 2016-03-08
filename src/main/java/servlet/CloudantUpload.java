import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


public class CloudantUpload extends HttpServlet 
{
	private CloudantClientClass db = new CloudantClientClass();

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	String jsonString = "";
    	int result;

    	try {
			List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
			for (FileItem item : items) {
				if (!item.isFormField()) 
				{
					// item is the file (and not a field), read it in and add to List
					Scanner scanner = new Scanner(new InputStreamReader(item.getInputStream(), "UTF-8"));

					//converting uploaded file to json
					while (scanner.hasNextLine()) 
					{
						String line = scanner.nextLine().trim();
						if (line.length() > 0) 
						{
							jsonString += line;
						}
					}
					scanner.close();

					result = db.addEntry(jsonString);

					request.setAttribute("msg", "Entry Added!; Response code "+result);
					break;
				}
			}
			
		} catch (Exception e) {
			request.setAttribute("msg", e.getMessage());
			e.printStackTrace(System.err);
		}

		response.setContentType("text/html");
        response.setStatus(200);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}