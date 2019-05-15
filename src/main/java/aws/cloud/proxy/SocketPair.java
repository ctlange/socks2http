package aws.cloud.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketPair {
	private final Socket client;
	private final InputStream is;
	private final OutputStream os;

	public SocketPair(Socket client, InputStream is, OutputStream os) {
		this.client = client;
		this.is = is;
		this.os = os;
	}

	public void pipe(SocketPair other) {
		pipe(this, other, is, other.os);
		pipe(other, this, other.is, os);
	}

	private static void pipe(SocketPair pair1, SocketPair pair2, InputStream is, OutputStream os) {
		new Thread(() -> {

			System.out.println("Start pipe.");
//			try {
//				IOUtils.copy(is, os);
//			} catch (IOException e) {
//			}
			pipe(is, os);
			close(pair1);
			close(pair2);
			System.out.println("Close pipe.");
		}).start();
	}

	private static void pipe(InputStream is, OutputStream os) {
		byte[] buffer = new byte[1024*1024];
		int len;
		try {
			while((len = is.read(buffer)) >= 0) {
				os.write(buffer, 0, len);
			}
		} catch (IOException e) {
		}
	}

	private static void close(SocketPair pair) {
		try {
			pair.is.close();
		} catch (IOException e) {
		}
		try {
			pair.os.close();
		} catch (IOException e) {
		}
		try {
			pair.client.close();
		} catch (IOException e) {
		}
	}
}
