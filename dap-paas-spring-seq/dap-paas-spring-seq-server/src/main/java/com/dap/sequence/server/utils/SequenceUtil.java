package com.dap.sequence.server.utils;


import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dap.sequence.server.core.SerialSeqProducer;

public class SequenceUtil {
    private static final Logger log = LoggerFactory.getLogger(SerialSeqProducer.class);

	  public static String getUUID32(){
		  return UUID.randomUUID().toString().replace("-", "").toUpperCase() ;
	  }
}
