package cqs.cn.android.bean;

/**
 * Created by bingo on 2020/12/15.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: 书签类
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/12/15
 */
public class Bookmark {
    public String name;
    public String tag;
    public String path;

    public Bookmark(String tag, String name, String path) {
        this.name = name;
        this.tag = tag;
        this.path = path;
    }
}
