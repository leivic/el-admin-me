package me.zhengjie.modules.qe.polo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EnvironmentItem {

    private String item; //每一项的名字
    private double fraction; //每一项的分值
}
