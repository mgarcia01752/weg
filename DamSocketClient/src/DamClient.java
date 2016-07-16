import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class DamClient {

	public static void main(String[] args) throws UnknownHostException, IOException {

		String sServerAddress = "10.1.10.10";
		int iPort = 5000;
		String sSendCommand = "101";
	
		int iCount = 0;
		
		while (iCount < 10) {
			
			Socket socket = new Socket(sServerAddress, iPort);

			/* Send Command to DAM*/
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			bw.write(sSendCommand);
			bw.newLine();
			bw.flush();

			/* Receive DAM Message */
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			System.out.println("Read From DAM -> " + reader.readLine() + " - Count -> " + iCount);
			reader.close();
			bw.close();
			
			socket.close();
			
			iCount++;
		}

	}



}
