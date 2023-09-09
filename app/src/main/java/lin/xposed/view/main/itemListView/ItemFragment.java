package lin.xposed.view.main.itemListView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import lin.xposed.R;
import lin.xposed.view.main.LinearSpacingItemDecoration;
import lin.xposed.view.main.MainAdapter;
import lin.xposed.view.main.MainSettingActivity;
import lin.xposed.view.main.itemview.info.DirectoryUiInfo;
import lin.xposed.view.main.itemview.info.ItemUiInfoGroupWrapper;

public class ItemFragment extends Fragment {
    private final DirectoryUiInfo directoryUiInfo;
    private ArrayList<Object> dataList;

    //整理非首次的数据
    private void sortData() {
        if (dataList != null) return;
        dataList = new ArrayList<>();
        for (ItemUiInfoGroupWrapper groupWrapper : directoryUiInfo.groupWrapperList) {
            dataList.add(groupWrapper);
            dataList.addAll(groupWrapper.getDirectoryUIInfoList());
        }
        dataList.addAll(directoryUiInfo.uiInfoList);
    }

    public ItemFragment(DirectoryUiInfo newDirInfo) {
        super();
        directoryUiInfo = newDirInfo;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View RootView = inflater.inflate(R.layout.item_list_layout, container, false);
        RecyclerView recyclerView = RootView.findViewById(R.id.item_list_view);

        MainAdapter adapter = new MainAdapter();
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("TAG")) {
            sortData();
            adapter.setDataList(dataList);
        }
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        LinearSpacingItemDecoration linearSpacingItemDecoration = new LinearSpacingItemDecoration();
        recyclerView.addItemDecoration(linearSpacingItemDecoration);

        //标题栏返回主界面
        TextView titleLeft = MainSettingActivity.leftText;
        titleLeft.setOnClickListener(v -> ItemFragment.this.getActivity().onBackPressed());
        return RootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        MainSettingActivity.setTitleCenterText(getString(R.string.app_name));
    }
}
