//package easyJava.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.klaytn.caver.Caver;
//import com.klaytn.caver.methods.response.Bytes32;
//import com.klaytn.caver.methods.response.TransactionReceipt;
//import com.klaytn.caver.transaction.response.PollingTransactionReceiptProcessor;
//import com.klaytn.caver.transaction.response.TransactionReceiptProcessor;
//import com.klaytn.caver.transaction.type.ValueTransfer;
//import com.klaytn.caver.wallet.keyring.AbstractKeyring;
//import com.klaytn.caver.wallet.keyring.KeyStore;
//import easyJava.dao.master.BaseDao;
//import easyJava.entity.BaseModel;
//import easyJava.entity.ResponseEntity;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.web3j.crypto.CipherException;
//import org.web3j.protocol.ObjectMapperFactory;
//import org.web3j.protocol.exceptions.TransactionException;
//
//import java.io.File;
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.math.BigInteger;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//public class KlayController {
//	@Autowired
//	BaseDao baseDao;
//
//	@Autowired
//	private RedisTemplate<String, Object> redisTemplate;
//
//	public static final String Klay_MANAGE = "address_invite";
//	public void sendingKLAY() throws IOException, CipherException, TransactionException {
//		Caver caver = new Caver(Caver.BAOBAB_URL);
//
//		//Read keystore json file.
//		File file = new File("./keystore.json");
//
//		//Decrypt keystore.
//		ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
//		KeyStore keyStore = objectMapper.readValue(file, KeyStore.class);
//		AbstractKeyring keyring = caver.wallet."").decrypt(keyStore, "password");
//
//		//Add to caver wallet.
//		caver.wallet.add(keyring);
//
//		BigInteger value = new BigInteger(caver.wallet..convertToPeb(BigDecimal.ONE, "KLAY"));
//
//		//Create a value transfer transaction
//		ValueTransfer valueTransfer = caver.transaction.valueTransfer.create(
//				TxPropertyBuilder.valueTransfer()
//						.setFrom(keyring.getAddress())
//						.setTo("0x8084fed6b1847448c24692470fc3b2ed87f9eb47")
//						.setValue(value)
//						.setGas(BigInteger.valueOf(25000))
//		);
//
//		//Sign to the transaction
//		valueTransfer.sign(keyring);
//
//		//Send a transaction to the klaytn blockchain platform (Klaytn)
//		Bytes32 result = caver.rpc.klay.sendRawTransaction(valueTransfer.getRawTransaction()).send();
//		if(result.hasError()) {
//			throw new RuntimeException(result.getError().getMessage());
//		}
//
//		//Check transaction receipt.
//		TransactionReceiptProcessor transactionReceiptProcessor = new PollingTransactionReceiptProcessor(caver, 1000, 15);
//		TransactionReceipt.TransactionReceiptData transactionReceipt = transactionReceiptProcessor.waitForTransactionReceipt(result.getResult());
//	}
//	@RequestMapping("/getKlayList")
//	public ResponseEntity<?> login(@RequestParam Map<String, Object> map) {
//		if (map.get("pageSize") == null || map.get("pageSize").toString().length() == 0) {
//			return new ResponseEntity(400, "pageSize不能为空！");
//		}
//		if (map.get("pageNo") == null || map.get("pageNo").toString().length() == 0) {
//			return new ResponseEntity(400, "pageNo不能为空！");
//		}
//		map.put("tableName", Klay_MANAGE);
//		BaseModel baseModel = new BaseModel();
//		baseModel.setPageSize(Integer.parseInt(map.get("pageSize").toString()));
//		baseModel.setPageNo(Integer.parseInt(map.get("pageNo").toString()));
//		var retmap = new HashMap();
//		var list = baseDao.selectBaseList(map, baseModel);
//		int count = baseDao.selectBaseCount(map);
//		retmap.put("list", list);
//		return new ResponseEntity(retmap, count, baseModel);
//	}
//
//	@RequestMapping("/insertKlay")
//	public ResponseEntity insertKlay(@RequestParam Map<String, Object> map) {
//		if (map.get("address") == null || map.get("address").toString().length() == 0) {
//			return new ResponseEntity(400, "address不能为空！");
//		}
//		map.put("tableName", Klay_MANAGE);
//		int count = baseDao.insertIgnoreBase(map);
//		return new ResponseEntity(count);
//	}
//
//	@RequestMapping("/updateKlay")
//	public ResponseEntity updateBaseByPrimaryKey(@RequestParam Map<String, Object> map) {
//		if (map.get("id") == null || map.get("id").toString().trim().length() == 0) {
//			return new ResponseEntity(400, "id不能为空！");
//		}
//		map.put("tableName", Klay_MANAGE);
//		int count = baseDao.updateBaseByPrimaryKey(map);
//		return new ResponseEntity(count);
//	}
//
//	@RequestMapping("/getKlay")
//	public ResponseEntity getKlay(@RequestParam Map<String, Object> map) {
//		if (map.get("address") == null || map.get("address").toString().length() == 0) {
//			return new ResponseEntity(400, "address不能为空！");
//		}
//		map.put("tableName", Klay_MANAGE);
//		BaseModel baseModel = new BaseModel();
//		baseModel.setPageSize(1);
//		baseModel.setPageNo(1);
//		HashMap retmap = new HashMap();
//		List list = baseDao.selectBaseList(map, baseModel);
//		retmap.put("list", list);
//		return new ResponseEntity(retmap, 1, baseModel);
//	}
//}
