package com.example.todolist.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * ViewPager加载Fragment适配器
 * @author Algotithm
 */
public class FragmentAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragmentList;
    private List<String> tab_titles;

    //构造
    public FragmentAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> titles) {
        super(fm);
        this.fragmentList = fragmentList;
        this.tab_titles = titles;
    }

    /**
     * 获取Fragment
     * @param position 位置
     * @return fragment
     */
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    /**
     * 获取总量
     * @return fragment数量
     */
    @Override
    public int getCount() {
        return fragmentList.size();
    }

    /**
     * 获取tab_title
     * @param position 标题位置
     * @return 返回标题内容
     */
    @Override
    public CharSequence getPageTitle(int position){
        return tab_titles.get(position);
    }
}


