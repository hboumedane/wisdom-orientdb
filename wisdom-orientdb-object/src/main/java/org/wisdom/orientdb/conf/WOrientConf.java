package org.wisdom.orientdb.conf;

import org.wisdom.api.configuration.Configuration;

import java.util.*;

import static com.orientechnologies.orient.core.tx.OTransaction.TXTYPE;

/**
 * Convenient Object used in order to set up an {@link org.wisdom.orientdb.object.OrientDbRepository}.
 *
 * It must contains:
 * - alias, the repository alias
 * - url, the database url
 * - user, the database user name
 * - pass, the database password
 * - nameSpace, the package name of the entities class.
 */
public final class WOrientConf {
    public static final String ORIENTDB_PREFIX = "orientdb";
    public static final String ORIENTDB_URL = "url";
    public static final String ORIENTDB_USER = "user";
    public static final String ORIENTDB_PASS = "pass";
    public static final String ORIENTDB_PACKAGE = "package";
    public static final String ORIENTDB_AUTOLAZYLOADGING = "autolazyloading";
    public static final String ORIENTDB_TXTYPE = "txtype";
    public static final String ORIENTDB_POOLMIN = "poolmin";
    public static final String ORIENTDB_POOLMAX = "poolmax";

    private final String alias;
    private final String url;
    private final String user;
    private final String pass;
    private final List<String> nameSpaces;

    //with default value
    private Boolean autolazyloading = true;
    private TXTYPE txtype = TXTYPE.OPTIMISTIC;
    private Integer poolMin = 1;
    private Integer poolMax = 20;



    public WOrientConf(String alias, String url, String user, String pass, List<String> nameSpace) {
        this.alias = alias;
        this.url = url;
        this.user = user;
        this.pass = pass;
        this.nameSpaces = nameSpace;
    }

    private WOrientConf(String alias, Configuration config) {
        this(
            alias,
            config.getOrDie(ORIENTDB_URL),
            config.getOrDie(ORIENTDB_USER),
            config.getOrDie(ORIENTDB_PASS),
            config.getList(ORIENTDB_PACKAGE)
        );
        this.setAutoLazyLoading(config.getBooleanWithDefault(ORIENTDB_AUTOLAZYLOADGING,autolazyloading));
        this.setTxType(config.get(ORIENTDB_TXTYPE, TXTYPE.class, txtype));
        this.setPoolMin(config.getIntegerWithDefault(ORIENTDB_POOLMIN,poolMin));
        this.setPoolMax(config.getIntegerWithDefault(ORIENTDB_POOLMAX,poolMax));
    }

    public String getAlias() {
        return alias;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public List<String> getNameSpace() {
        return nameSpaces;
    }

    public TXTYPE getTxType() {
        return  txtype;
    }

    public void setTxType(TXTYPE type) {
        if(type == null){
            throw new NullPointerException(ORIENTDB_TXTYPE+" cannot be null");
        }

        this.txtype = type;
    }

    public Boolean getAutolazyloading() {
        return autolazyloading;
    }

    public void setAutoLazyLoading(Boolean lazyloading){
        if(lazyloading == null){
            throw new NullPointerException(ORIENTDB_AUTOLAZYLOADGING+" cannot be null.");
        }

        this.autolazyloading = lazyloading;
    }

    public Integer getPoolMin(){
        return poolMin;
    }

    public Integer getPoolMax(){
        return poolMax;
    }

    public void setPoolMin(Integer min){
        poolMin = min;
    }

    public void setPoolMax(Integer max){
        poolMax = max;
    }

    public Dictionary<String,Object> toDico(){
        Dictionary<String,Object> dico = new Hashtable<>(5);
        dico.put("name",alias);
        dico.put(ORIENTDB_URL,url);
        dico.put(ORIENTDB_USER,user);
        dico.put(ORIENTDB_PACKAGE,nameSpaces);
        dico.put(ORIENTDB_AUTOLAZYLOADGING,autolazyloading);
        dico.put(ORIENTDB_TXTYPE,txtype);
        dico.put(ORIENTDB_POOLMIN,poolMin);
        dico.put(ORIENTDB_POOLMAX,poolMax);
        return dico;
    }

    /**
     * Extract all WOrientConf configuration from the parent configuration.
     * If the configuration is
     * orientdb.default.url = "plocal:/home/wisdom/db"
     * orientdb.test.url = "plocal:/home/wisdom/test/db"
     * <p/>
     * the sub configuration will be:
     * <p/>
     * [alias:default]
     * url = "plocal:/home/wisdom/db"
     * [alias:test]
     * url = "plocal:/home/wisdom/test/db"
     *
     * @param config
     * @return A Collection of WOrientConf retrieve from the Wisdom configuration file.
     */
    public static Collection<WOrientConf> createFromApplicationConf(Configuration config) {
        Configuration orient = config.getConfiguration(ORIENTDB_PREFIX);

        if(orient == null){
            return Collections.EMPTY_SET;
        }

        Set<String> subkeys = new HashSet<>();

        for (String key : orient.asMap().keySet()) {
            subkeys.add(key);
        }

        Collection<WOrientConf> subconfs = new ArrayList<>(subkeys.size());

        for (String subkey : subkeys) {
            subconfs.add(new WOrientConf(subkey, orient.getConfiguration(subkey)));
        }

        return subconfs;
    }

    @Override
    public String toString() {
        return "WOrientConf{" +
                "alias='" + alias + '\'' +
                ", url='" + url + '\'' +
                ", user='" + user + '\'' +
                ", nameSpace='" + nameSpaces.toString()+ '\'' +
                ", autolazyloading=" + autolazyloading + '\'' +
                ", txtype=" + txtype +
                ", poolmin='" + poolMin + '\'' +
                ", poolmax='" + poolMax + '\'' +
                '}';
    }
}