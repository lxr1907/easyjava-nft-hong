package easyJava.etherScan;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public interface ScanService {

    List<Map> doScan() ;
    List<Map> doScanAddress() ;

}
