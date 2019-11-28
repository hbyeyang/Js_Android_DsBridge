package com.xszn.ime.bean;

import java.util.List;

/**
 * @author yeyang
 * @name qskd_tip_demo
 * @class name：com.xszn.ime.bean
 * @class describe
 * @time 2019-11-22 17:31
 * @change
 * @chang time
 * @class describe
 */
public class PlayCodeBean {

    /**
     * adconfig : [{"type":"C","sub_type":"C_FULLVIDEO","code":915815986}]
     * name : pos3
     */

    private String name;
    private List<AdconfigEntity> adconfig;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AdconfigEntity> getAdconfig() {
        return adconfig;
    }

    public void setAdconfig(List<AdconfigEntity> adconfig) {
        this.adconfig = adconfig;
    }

    @Override
    public String toString() {
        return "PlayCodeBean{" +
                "name='" + name + '\'' +
                ", adconfig=" + adconfig +
                '}';
    }

    public static class AdconfigEntity {
        /**
         * type : C
         * sub_type : C_FULLVIDEO
         * code : 915815986
         */

        private String type;
        private String sub_type;
        private int code;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSub_type() {
            return sub_type;
        }

        public void setSub_type(String sub_type) {
            this.sub_type = sub_type;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return "AdconfigEntity{" +
                    "type='" + type + '\'' +
                    ", sub_type='" + sub_type + '\'' +
                    ", code=" + code +
                    '}';
        }
    }
}
