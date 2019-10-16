package com.mq.serialize;

import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.mq.model.message.msgnet.RequestMessage;
import com.mq.model.message.msgnet.ResponseMessage;

/**
 * KryoPool池工厂
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月20日 上午10:18:54
 */
public class KryoPoolFactory {

    /**
     * kryo对象生成工厂
     */
    private static KryoFactory factory = new KryoFactory() {
        public Kryo create() {
            Kryo kryo = new Kryo();
            kryo.setReferences(false);
            kryo.register(RequestMessage.class);
            kryo.register(ResponseMessage.class);
            kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
            return kryo;
        }
    };

    /**
     * kryo生成池
     */
    private static KryoPool pool = new KryoPool.Builder(factory).build();

    private KryoPoolFactory() {
    }

    /**
     * 获取kryo池对象
     * 
     * @return KryoPool
     * @date: 2019年9月20日 上午10:18:05
     */
    public static KryoPool getKryoPool() {
        return pool;
    }

    /** 实例化kyro对象，kyro本身不是线程安全的，有两种选择来保证线程安全，ThreadLocal和kyro提供的池 */
    /**
     * ************************1，显示指定初始化器****************************
     * 在上面注意到kryo.setInstantiatorStrategy(new
     * Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
     * 这句话显示指定了实例化器。
     * 
     * 在一些依赖了kryo的开源软件中，可能由于实例化器指定的问题而抛出空指针异常。例如hive的某些版本中，默认指定了StdInstantiatorStrategy。
     * 而StdInstantiatorStrategy在是依据JVM version信息及JVM
     * vendor信息创建对象的，可以不调用对象的任何构造方法创建对象。
     * 那么例如碰到ArrayList这样的对象时候，就会出问题。观察一下ArrayList的源码：
     * 
     * public ArrayList() { this.elementData =
     * DEFAULTCAPACITY_EMPTY_ELEMENTDATA; }
     * 
     * 既然没有调用构造器，那么这里elementData会是NULL，那么在调用类似ensureCapacity方法时，就会抛出一个异常。
     * 
     * 解决方案很简单，就如框架中代码写的一样，显示指定实例化器，首先使用默认无参构造策略DefaultInstantiatorStrategy，若创建对象失败再采用StdInstantiatorStrategy。
     */
    private static final ThreadLocal<Kryo> kryoLocal = new ThreadLocal<Kryo>() {
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
            return kryo;
        }
    };

    private static KryoPool newKryoPool() {
        // Builder方法传入一个生成kryo对象的工厂
        return new KryoPool.Builder(() -> {
            final Kryo kryo = new Kryo();
            kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
            kryo.setReferences(false);// 不检查循环引用（不存在循环应用问题，可以提高点性能）
            return kryo;
        }).softReferences().build();
    }

    public static void main(String[] args) {

    }
}
