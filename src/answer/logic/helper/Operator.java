package answer.logic.helper;

import java.io.UnsupportedEncodingException;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xvolks.jnative.JNative;
import org.xvolks.jnative.Type;
import org.xvolks.jnative.exceptions.NativeException;
import org.xvolks.jnative.misc.basicStructures.HWND;
import org.xvolks.jnative.pointers.Pointer;
import org.xvolks.jnative.pointers.memory.MemoryBlockFactory;

import answer.exception.Axexception;

import answer.util.BeanPrinter;

import answer.ax.output.UserInfo;
import answer.util.IP;

@Component
public class Operator {

    @Autowired
    protected PopertiesHelper popertiesHelper = null;
    public boolean islogin = false;
    private Logger log = Logger.getLogger(Operator.class);

    public Operator() {
        Thread thr3 = new Thread() {

            @Override
            public void run() {
                while (true) {
                    log.info("check network status");
                    if (!IP.isNetworkAvailable()) {
                        islogin = false;
                        log.info(" network is down");
                    }else {
                        log.info(" network is ok");
                    }
                    try {
                        Thread.sleep(60 * 1000);
                    } catch (InterruptedException ex) {
                        log.info(ex.getMessage());
                    }
                }

            }
        ;
        }; 
        thr3.start();
    }

    public int intitAndlogin() throws Axexception, NativeException {

        PropertiesConfiguration pconfig = popertiesHelper.getStocksProperties();

        System.loadLibrary(pconfig.getString("axdll_path"));

        JNative jnative = null;

        try {
            jnative = new JNative("AxStock.dll", "AxS_Init");
            jnative.setRetVal(Type.INT);
            HWND parent = new HWND(4);
            int k = 0;
            jnative.setParameter(k++, parent.createPointer());
            jnative.setParameter(k++, 1);
            jnative.invoke();
            log.info("init dll succsesfully");
        } catch (IllegalAccessException | NativeException e) {
            log.info(e.getMessage());
            e.printStackTrace();
        }


        try {
            Pointer pointer = null;
            jnative = new JNative("AxStock.dll", "AxS_Login");
            jnative.setRetVal(Type.INT);
            int k = 0;
            pointer = new Pointer(MemoryBlockFactory.createMemoryBlock(30));
            pointer.setStringAt(0, pconfig.getString("ax_ip"));
            jnative.setParameter(k++, pointer);

            jnative.setParameter(k++, pconfig.getInt("ax_port"));
            jnative.setParameter(k++, '0');

            pointer = new Pointer(MemoryBlockFactory.createMemoryBlock(30));
            pointer.setStringAt(0, pconfig.getString("ax_account"));

            jnative.setParameter(k++, pointer);

            pointer = new Pointer(MemoryBlockFactory.createMemoryBlock(30));
            pointer.setStringAt(0, pconfig.getString("ax_pswd"));
            jnative.setParameter(k++, pointer);

            pointer = new Pointer(MemoryBlockFactory.createMemoryBlock(8));
            pointer.setIntAt(0, 1);
            jnative.setParameter(k++, pointer);

            pointer = new Pointer(MemoryBlockFactory.createMemoryBlock(160));
            jnative.setParameter(k++, pointer);

            jnative.invoke();

            if (jnative.getRetValAsInt() != 0) {
                throw new Axexception("login failed");
            }
            MemoCach.setUsf(new UserInfo(pointer, popertiesHelper.getStocksProperties().getString("ax_encode")));

            pointer = null;
            log.info(BeanPrinter.dump(MemoCach.getUsf()));
            islogin = true;
            log.info("login succsesfully");

        } catch (NativeException | IllegalAccessException e) {
            log.info(e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public int axS_Entrust(Pointer input, Pointer output)  {
        PropertiesConfiguration pconfig = popertiesHelper.getStocksProperties();

        System.loadLibrary(pconfig.getString("axdll_path"));
        JNative fnc;
        try {

            fnc = new JNative("AxStock.dll", "AxS_Entrust");
            fnc.setRetVal(Type.INT);
            fnc.setParameter(0, input);
            fnc.setParameter(1, output);
            fnc.invoke();
            log.info("axS_Entrust has been invoked succsesfully");
            return fnc.getRetValAsInt();

        } catch (NativeException | IllegalAccessException e) {

            log.info(e.getMessage());
            e.printStackTrace();
        }
        return -99;
    }

    public int axS_QueryBalance(char Currency, Pointer len, Pointer output)  {

        PropertiesConfiguration pconfig = popertiesHelper.getStocksProperties();

        System.loadLibrary(pconfig.getString("axdll_path"));
        JNative fnc;
        try {
            fnc = new JNative("AxStock.dll", "AxS_QueryBalance");
            fnc.setRetVal(Type.INT);
            fnc.setParameter(0, Currency);

            fnc.setParameter(1, len);
            fnc.setParameter(2, output);
            fnc.invoke();
            log.info("axS_QueryBalance has been invoked succsesfully");

            return fnc.getRetValAsInt();
        } catch (NativeException | IllegalAccessException e) {
            log.info(e.getMessage());
            e.printStackTrace();
        }
        return -99;
    }

    public int axS_QueryEntrust(Pointer input, Pointer len, Pointer output)  {

        JNative fnc;
        try {
            fnc = new JNative("AxStock.dll", "AxS_QueryEntrust");
            fnc.setRetVal(Type.INT);


            fnc.setParameter(0, input);
            fnc.setParameter(1, len);
            fnc.setParameter(2, output);

            fnc.invoke();
            log.info("axS_QueryEntrust has been invoked succsesfully");
            return fnc.getRetValAsInt();
        } catch (NativeException | IllegalAccessException e) {
            log.info(e.getMessage());
            e.printStackTrace();
        }
        return -99;
    }

    public int axS_QueryDeals(Pointer input, int len, Pointer output)  {

        JNative fnc;
        try {
            fnc = new JNative("AxStock.dll", "AxS_QueryDeals");
            fnc.setRetVal(Type.INT);


            fnc.setParameter(0, input);
            fnc.setParameter(1, len);
            fnc.setParameter(2, output);

            fnc.invoke();
            log.info("axS_QueryDeals has been invoked succsesfully");
            return fnc.getRetValAsInt();
        } catch (NativeException | IllegalAccessException e) {
            log.info(e.getMessage());
            e.printStackTrace();
        }
        return -99;
    }

    public String axS_GetErrorDesc()  {

        JNative fnc;
        try {
            fnc = new JNative("AxStock.dll", "AxS_GetErrorDesc");
            fnc.setRetVal(Type.PSTRUCT);
            fnc.invoke();
            log.info("axS_GetErrorDesc has been invoked succsesfully");
            return fnc.getRetVal();
        } catch (NativeException | IllegalAccessException e) {
            log.info(e.getMessage());
            e.printStackTrace();
        }
        return "-99";
    }
}
