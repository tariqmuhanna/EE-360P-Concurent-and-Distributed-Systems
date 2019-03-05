import java.io.IOException;

public class ClientTester {
	public static void main(String[] args) {

		String[] input = new String[2];
		String file = "cmdFile";
		input[0] = file;
		for(int i = 0; i < 10; i++) {
			String id = String.valueOf(i);
			input[1] = id;
			CarClient x = new CarClient();
			try {
				x.main(input);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
