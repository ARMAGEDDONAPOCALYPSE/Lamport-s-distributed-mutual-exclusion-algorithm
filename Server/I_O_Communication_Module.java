import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class I_O_Communication_Module implements Runnable
{
    Socket active_socket = null;
    Operator op = null;

    public I_O_Communication_Module(Socket active_connection, Operator o)
    {
        active_socket = active_connection;
        op = o;
    }

    @Override
    public void run()
    {
        if (active_socket == null) return;
        try
        {
            BufferedReader income_client =
                    new BufferedReader(new InputStreamReader(active_socket.getInputStream()));
            DataOutputStream outgoing = new DataOutputStream(active_socket.getOutputStream());

            String operations = income_client.readLine();

            if (Server.DEBUG && Server.VERBOSE)
            	System.out.println("DEBUG server>client run()");
            String server_response = op.execute_operation(operations);

            outgoing.write(server_response.getBytes());

            outgoing.close();
            income_client.close();
            active_socket.close();

            return;

        } catch (IOException e)
        {
            System.out.println("Error communicating with client.");
        } catch (Operator.Operation_Not_Found op)
        {
            if (Server.DEBUG) System.out.println("Client sent a NOT FOUND operation: " + op.getMessage());
        }


    }
}
