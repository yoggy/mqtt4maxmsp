import com.cycling74.max.*;
import org.eclipse.paho.client.mqttv3.*;
import java.util.UUID;

public class mqttsub extends MaxObject implements MqttCallback {

	String client_id = "mqtt4maxmsp_" + UUID.randomUUID();

	MqttClient client;

	String host;
	int port;
	String topic;

	public mqttsub(Atom[] args) {
		declareInlets(new int[]{DataTypes.ALL});
		declareOutlets(new int[]{DataTypes.ALL});
		
		if (args.length < 2) {
			clearParams();
		}
		else if (args.length == 2) {
			// args : host topic
			host  = args[0].getString();
			port  = 1883;
			topic = args[1].getString();
			connectSession();
		}
		else if (args.length == 3) {
			// args : host port topic
			host  = args[0].getString();
			port  = args[1].getInt();
			topic = args[2].getString();
			connectSession();
		}
		else {
			clearParams();
		}
	}
	
	@Override
	protected void	notifyDeleted() {
		closeSession();
	}
	
	private void clearParams() {
		closeSession();
		
		host  = "";
		port  = 1883;
		topic = "";
	}
	

	private void connectSession() {
		closeSession();
		
		try {
			String url = "tcp://" + host + ":" + port;
			client = new MqttClient(url, client_id);

			MqttConnectOptions opts = new MqttConnectOptions();
			opts.setCleanSession(true);
			opts.setConnectionTimeout(10); // ’PˆÊ‚Í•b
			opts.setKeepAliveInterval(60);

			client.setCallback(this);
			client.connect(opts);
			
			client.subscribe(topic);
        }
		catch (Exception e) {
			error("exception occured in connectSession()");
			showException(e);
			closeSession();
		}
	}

	private void closeSession() {
		if (client != null) {
			try {
				client.disconnect();
			}
			catch (Exception e) {
				showException(e);
			}
			client = null;
		}
	}
	
	@Override
	public void connectionLost(Throwable throwable) {
		closeSession();
	}

	@Override
	public void messageArrived(String topic, MqttMessage msg) throws Exception {
	    String str = new String(msg.getPayload());
	    outlet(0, str);
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
	}
}