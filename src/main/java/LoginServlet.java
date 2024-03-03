import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String text = request.getParameter("command");

        File file = new File("c:/Program Files/apache-tomcat-9.0.85/bin", "Main.class");
        File file2 = new File("c:/Program Files/apache-tomcat-9.0.85/bin", "Main.java");
        
        // Check if the file exists
        if (file.exists()) {
            // Attempt to delete the file
           file.delete();
        } 
        if (file2.exists()) {
            // Attempt to delete the file
           file2.delete();
        } 
        if (text != null && !text.isEmpty()) {
            try (FileWriter writer = new FileWriter("Main.java")) {
                writer.write(text);
            } catch (IOException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Error writing to file: " + e.getMessage());
                return;
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("OUTPUT:-");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("No text provided in the request.");
        }

        response.setContentType("text/plain");
        String output;
        
        // Compile Main.java
        boolean compilationSuccess = compileMainJavaFile(response);
        if (compilationSuccess) {
            // Run Main.class
            output = runMainClass(response);
        } else {
            output = "Compilation failed.";
        }

        response.getWriter().write("\n\n\n"+output);
        
    }

    private boolean compileMainJavaFile(HttpServletResponse response) throws IOException {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("javac", "Main.java");
            Process process = processBuilder.directory(new File("c:/Program Files/apache-tomcat-9.0.85/bin")).start(); // Replace "/path/to/directory" with the directory containing Main.java
            int exitCode = process.waitFor();
            return exitCode == 0; // Compilation succeeded if exit code is 0
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            
            return false;
        }
    }

    private String runMainClass(HttpServletResponse response) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("java", "-cp", "c:/Program Files/apache-tomcat-9.0.85/bin", "Main");
            Process process2 = processBuilder.start(); // Replace "/path/to/directory" with the directory containing Main.class
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process2.getInputStream()))) {
                

                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append('\n');
                }
            }catch(Exception e){
                response.getWriter().println(e.getMessage());
            }
            int exitCode = process2.waitFor();
            output.append("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\t\t\t\t\tExit code: ").append(exitCode);
            return output.toString();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error executing Main.java: " + e.getMessage();
        }
       }
    
}
