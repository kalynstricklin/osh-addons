package org.sensorhub.test.sensor.trek1000;

import static org.junit.Assert.*;
import java.io.IOException;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.sensorhub.api.event.Event;
import org.sensorhub.api.event.IEventListener;
import org.sensorhub.api.data.IStreamingDataInterface;
import org.sensorhub.api.data.DataEvent;
import org.sensorhub.impl.comm.rxtx.RxtxSerialCommProviderConfig;
import org.sensorhub.impl.sensor.trek1000.Trek1000Config;
import org.sensorhub.impl.sensor.trek1000.Trek1000Sensor;
import org.vast.data.TextEncodingImpl;
import org.vast.sensorML.SMLUtils;
import org.vast.swe.AsciiDataWriter;
import org.vast.swe.SWEUtils;
import net.opengis.sensorml.v20.AbstractProcess;
import net.opengis.swe.v20.DataComponent;


public class TestTrek1000Rxtx implements IEventListener
{
	Trek1000Sensor driver;
	Trek1000Config config;
	AsciiDataWriter writer;
    int sampleCount = 0;
	
	
	@Before
	public void init() throws Exception {
		config = new Trek1000Config();
		config.id = UUID.randomUUID().toString();
		
		RxtxSerialCommProviderConfig providerConf = new RxtxSerialCommProviderConfig();
        providerConf.moduleClass = "org.sensorhub.impl.comm.rxtx.RxtxSerialCommProvider";
        providerConf.protocol.portName = "/dev/ttyACM0";
        providerConf.protocol.baudRate = 9600;
        config.commSettings = providerConf;
		
		driver = new Trek1000Sensor();
		driver.setConfiguration(config);
		driver.init();
	}

	@Test
	public void testGetOutputDesc() throws Exception
	{
		for (IStreamingDataInterface di: driver.getObservationOutputs().values())
		{
			System.out.println();
			DataComponent dataMsg = di.getRecordDescription();
            new SWEUtils(SWEUtils.V2_0).writeComponent(System.out, dataMsg, false, true);
		}
	}
	

	@Test
	public void testGetSensorDesc() throws Exception
	{
		System.out.println();
		AbstractProcess smlDesc = driver.getCurrentDescription();
		new SMLUtils(SWEUtils.V2_0).writeProcess(System.out, smlDesc, true);
	}
    
    
    @Test
    public void testSendMeasurements() throws Exception
    {
        System.out.println();
                
        writer = new AsciiDataWriter();
        writer.setDataEncoding(new TextEncodingImpl(",", "\n"));
        writer.setOutput(System.out);
        
        //IStreamingDataInterface rangeOutput = driver.getObservationOutputs().get("ranges");
        //rangeOutput.registerListener(this);
        IStreamingDataInterface locOutput = driver.getObservationOutputs().get("xyzLoc");
        locOutput.registerListener(this);
        
        driver.start();
        
        synchronized (this) 
        {
            while (sampleCount < 20)
                wait();
        }
        
        System.out.println();
    }
    
    
    @Override
    public void handleEvent(Event e)
    {
        assertTrue(e instanceof DataEvent);
        DataEvent dataEvent = (DataEvent)e;
        
        try
        {
            IStreamingDataInterface output = driver.getObservationOutputs().get(dataEvent.getOutputName());
            writer.setDataComponents(output.getRecordDescription().copy());
            writer.reset();
            writer.write(dataEvent.getRecords()[0]);
            writer.flush();
            
            sampleCount++;
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
                
        synchronized (this) { this.notify(); }
    }
}
