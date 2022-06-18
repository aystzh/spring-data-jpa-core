package aystzh.github.com.jpa.common.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by zhanghuan on 2022/6/18.
 */
@Data
@MappedSuperclass
public class BaseEntity implements Serializable {


    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "VARCHAR(36) COMMENT 'ID'")
    private String id;

    /**
     * 创建者Id
     */
    @Column(name = "create_by", columnDefinition = "VARCHAR(50) COMMENT '创建者Id'")
    private String createdBy;

    /**
     * 创建时间
     */
    @Column(name = "create_date", columnDefinition = "DATETIME COMMENT '创建时间'")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDate;

    /**
     * 最后更新人Id
     */
    @Column(name = "update_by", columnDefinition = "VARCHAR(50) COMMENT '更新人Id'")
    protected String updateBy;


    /**
     * 最后更新时间
     */
    @Column(name = "update_date", columnDefinition = "DATETIME COMMENT '最后更新时间'")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateDate;

    /**
     * 是否删除标识
     * 0：否
     * 1: 是
     */
    @Column(name = "delete_flag", columnDefinition = "tinyint(1) DEFAULT 0 COMMENT '是否删除标识 0-否 1-是'")
    private Integer deleteFlag;

    @Transient
    private Integer page = 1;
    @Transient
    private Integer size = 20;

    @PrePersist
    public void onCreate() {
        if (this.getCreateDate() == null) {
            createDate = new Date();
        }

        if (this.getCreatedBy() == null) {
            createdBy = "0";
        }
        if (this.getUpdateBy() == null) {
            updateBy = "0";
        }

        if (this.getUpdateDate() == null) {
            updateDate = new Date();
        }
        if (this.getDeleteFlag() == null) {
            deleteFlag = 0;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updateDate = new Date();

        if (this.getUpdateBy() == null) {
            updateBy = "0";
        }
        if (this.getDeleteFlag() == null) {
            deleteFlag = 0;
        }
    }


//    public String toLike(String value) {
//        if (!StringUtils.hasText(value)) {
//            value = "";
//        }
//        return "%" + value + "%";
//    }
}