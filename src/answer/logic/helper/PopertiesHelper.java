package answer.logic.helper;

import answer.bean.dto.Bankuai;
import answer.bean.dto.BankuaiStock;
import java.io.File;
import java.util.*;
import jp.terasoluna.fw.dao.QueryDAO;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PopertiesHelper {

    @Autowired
    protected PropertiesConfiguration stocksProperties = null;
    @Autowired
    protected QueryDAO queryDAO = null;
    private List<String> ls_shsz = null;

    public PropertiesConfiguration getStocksProperties() {
        return stocksProperties;
    }

    public void setStocksProperties(PropertiesConfiguration stocksProperties) {
        this.stocksProperties = stocksProperties;
    }

    public List<String> getStockCds() {

        if (ls_shsz != null) {
            return ls_shsz;
        }
        String datafile_path_sh = stocksProperties.getString("datafile_path_sh");
        String datafile_path_sz = stocksProperties.getString("datafile_path_sz");
        File filesh = new File(datafile_path_sh);
        File filesz = new File(datafile_path_sz);
        File[] filessh = filesh.listFiles();
        File[] filessz = filesz.listFiles();
        HashSet hs = new HashSet();
//        HashSet zhishu = new HashSet();
        hs.add("si000001");
        for (int i = 0; i < filessh.length; i++) {
            String fn = filessh[i].getName();


            if ((fn.startsWith("60") && fn.contains(".day"))) {
                hs.add(fn.substring(0, fn.indexOf(".")));

            }


        }

        for (int i = 0; i < filessz.length; i++) {
            String fn = filessz[i].getName();
            if ((fn.startsWith("30") || fn.startsWith("00")) && (fn.contains(".day"))) {
                hs.add(fn.substring(0, fn.indexOf(".")));

            }

        }


        List<BankuaiStock> bkslist = queryDAO.executeForObjectList("bankuaiStock.selectAllCond", null);
        for (BankuaiStock bks:bkslist){
            hs.add(bks.getStock_cd());
        }
        ls_shsz = new ArrayList(hs);


        return ls_shsz;
    }
}
