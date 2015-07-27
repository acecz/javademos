package demo.baidu.lbs.web.ctrl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import demo.baidu.lbs.cfg.WebCfg;

@Path(value = "test")
public class TestCtrl {
	// private static final Logger log = Logger.getLogger(TestCtrl.class);

	@GET
	@Path("get")
	@Produces({ MediaType.APPLICATION_JSON })
	public Map<String, String> testGet() {
		Map<String, String> respPo = new HashMap<String, String>();
		respPo.put(System.currentTimeMillis() + "", UUID.randomUUID().toString());
		return respPo;
	}

	@POST
	@Path("post")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Map<String, String> testPost(Map<String, String> inData) {
		inData.put(System.currentTimeMillis() + "", UUID.randomUUID().toString());
		return inData;
	}

	@GET
	@Path("cfg")
	@Produces({ MediaType.APPLICATION_JSON })
	public Map<String, String> getCfg() {
		return WebCfg.webCfg();
	}

}
