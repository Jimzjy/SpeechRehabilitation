package com.jimzjy.speechrehabilitation.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jimzjy.speechrehabilitation.R
import com.jimzjy.speechrehabilitation.common.ApplicationComponent
import com.jimzjy.speechrehabilitation.common.SRApplication
import com.jimzjy.speechrehabilitation.room.word.ROOM_SINGLE_WORD
import com.jimzjy.speechrehabilitation.room.word.ROOM_WORDS
import com.jimzjy.speechrehabilitation.room.word.Word
import com.jimzjy.speechrehabilitation.room.word.WordRepository
import com.jimzjy.speechrehabilitation.ui.main.MainActivity
import com.jimzjy.speechrehabilitation.ui.singleWord.SingleWordViewModel
import com.jimzjy.speechrehabilitation.ui.user.UserViewModel
import com.jimzjy.speechrehabilitation.ui.words.WordsViewModel
import dagger.Component
import javax.inject.Inject
import javax.inject.Scope

class StartActivity : AppCompatActivity() {
    @Inject
    lateinit var wordRepository: WordRepository

    init {
        DaggerStartActivityComponent.builder()
                .applicationComponent(SRApplication.applicationComponent)
                .build().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val preference = this.getSharedPreferences("basic", Context.MODE_PRIVATE)
        val version: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            applicationContext.packageManager.getPackageInfo(applicationContext.packageName, 0).longVersionCode.toInt()
        } else {
            @Suppress("DEPRECATION")
            applicationContext.packageManager.getPackageInfo(applicationContext.packageName, 0).versionCode
        }

        if (preference.getInt("version", 0) < version) {
            val singleWord = Word(ROOM_SINGLE_WORD,
                    "bā八 bá拔 bă把 bà爸|pā啪 pá爬 pà怕|mā妈 má麻 mă马 mà骂|fā发 fá罚 fă法 fà发|dā搭 dá答 dă打 dà大|tā他 tă塔 tà踏|ná拿 nă哪 nà那|lā拉 lă喇 là辣|gá嘎 gà尬|kā咖 kă卡|zhā扎 zhá闸 zhà炸|chā叉 chá茶 chà差|shā杀 shá啥 shă傻 shà厦|zā扎 cā擦 sā撒|să洒 sà萨|mā妈 bā八 má麻 bá拔|mā妈 pā啪 má麻|pá爬 mà骂 pà怕|mā妈 fā发 má麻 fá罚|mă马 fă法 mà骂 fà发|ná拿 dá答 nă哪|dă打 nà那 dà大|nă哪 tă塔|nà那 tà踏|ná拿 zá砸|ná拿 zhá闸 nà那 zhà炸|ná拿 chá茶 nà那 chà差|nă哪 să洒 nà那 sà萨|ná拿 shá啥 nă哪 shă傻|nà那 shà厦|lā拉 dā搭|lă喇 dă打 là辣 dà大|lā拉 tā他 lă喇 tă塔|là辣 tà踏|lā拉 cā擦")
            val words = Word(ROOM_WORDS,
                    "骚动 骚乱 骚扰|入户 入画 入会 入冬|瑞安 瑞德 瑞典 瑞士|锐角 锐意 若干 若恩|撒腿 撒网 洒放 洒满|萨摩 萨那 萨斯 腮红|赛道 赛季 赛里 赛跑|扫盲 扫描 扫兴 扫尾|森林 森然 僧侣 森严|砂糖 砂陶 砂土 砂型|砂纸 杀敌 杀毒 杀机|沙尘 沙岛 沙德 沙岗|沙坑 沙雷 沙曼 沙漠|煞车 煞住 筛检 筛子|晒种 珊瑚 杉树 杉木|山岛 山道 山地 山顶|山歌 山根 山沟 山谷|擅长 擅自 善舞 善用|商标 商场 商代 商店|上灯 上帝 上风 上供|上缴 上届 上课 上利|上签 上前 上升 上士|上颌 尚存 尚能 稍候|烧开 烧卖 烧炭 烧砖|身体 神经 深广 深谷|省界 圣火 失当 失落")

            wordRepository.insertWord(singleWord) {
                wordRepository.insertWord(words) {
                    val editor = preference.edit()
                    editor.putInt("version", version)
                    editor.apply()

                    choseUser(preference)
                }
            }
        } else {
            choseUser(preference)
        }
    }

    private fun choseUser(preferences: SharedPreferences) {
        val username = preferences.getString("username", "")
        if (username.isEmpty()) {
            startActivity(Intent(this@StartActivity, CreateUserActivity::class.java))
        } else {
            startActivity(Intent(this@StartActivity, MainActivity::class.java))
        }
    }
}

@StartActivityScope
@Component(dependencies = [ApplicationComponent::class])
interface StartActivityComponent {
    fun inject(startActivity: StartActivity)
    fun inject(createUserActivity: CreateUserActivity)
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class StartActivityScope
