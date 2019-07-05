package com.redefine.welike.business.mysetting.ui.adapter

import android.support.annotation.StringRes
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.redefine.foundation.language.LocalizationManager
import com.redefine.welike.MyApplication
import com.redefine.welike.R
import com.redefine.welike.base.LanguageSupportManager
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.business.startup.management.StartEventManager
import com.redefine.welike.business.startup.management.bean.LanguageBean
import com.redefine.welike.business.startup.management.constant.LanguageConstant
import java.util.HashMap


@StringRes
fun LanguageBean.getDisplayName(): Int {
    return when (this.shortName) {
        LocalizationManager.LANGUAGE_TYPE_ENG -> {
            R.string.regist_choose_language_english
        }
        LocalizationManager.LANGUAGE_TYPE_HINDI -> {
            R.string.regist_choose_language_hindi
        }
        LocalizationManager.LANGUAGE_TYPE_GUJARATI -> {
            R.string.regist_choose_language_gujarati
        }
        LocalizationManager.LANGUAGE_TYPE_PUNJABI -> {
            R.string.regist_choose_language_punjabi
        }
        LocalizationManager.LANGUAGE_TYPE_BENGALI -> {
            R.string.regist_choose_language_bengali
        }
        LocalizationManager.LANGUAGE_TYPE_MARATHI -> {
            R.string.regist_choose_language_marathi
        }
        LocalizationManager.LANGUAGE_TYPE_TELUGU -> {
            R.string.regist_choose_language_telugu
        }
        LocalizationManager.LANGUAGE_TYPE_MALAYALAM -> {
            R.string.regist_choose_language_malayalam
        }
        else -> {
            R.string.regist_choose_language_english
        }
    }
}

class SettingLanguageAdapter : BaseAdapter() {
    var currentLanguageType: String = LanguageSupportManager.getInstance().currentMenuLanguageType
    //    var big_item = mappingLanguageList(currentLanguageType)
    val data = LanguageConstant.initData().filter { it.isReady }

    override fun getCount() = data.size

    override fun getItem(position: Int) = position

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView
                ?: LayoutInflater.from(parent.context).inflate(R.layout.choose_language_list_item, null)
        val tv_language = view.findViewById<TextView>(R.id.tv_choose_language_type)
        val iv_Selected = view.findViewById<ImageView>(R.id.iv_choose_language_type)
        val bean = data[position]
        tv_language.setText(bean.getDisplayName())

        view.setOnClickListener {
            //            big_item = position
            currentLanguageType = bean.shortName
            notifyDataSetChanged()
        }

        iv_Selected.setImageResource(if (currentLanguageType.contentEquals(bean.shortName)) {
            R.drawable.setting_language_checked
        } else {
            R.drawable.setting_language_check
        })

        return view
    }
}