package me.zhengjie.modules.qe.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

@Table(name = "continue_importfile")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true) //开启链式调用注解 
public class ContinueFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String file_name;
    private String newfilename;
    private String file_type;
    private String file_date;
    private String zone;
    private String create_by;
    private Date create_time;
    private String status;
    private String path;
    private Integer downloadcounts;
}
