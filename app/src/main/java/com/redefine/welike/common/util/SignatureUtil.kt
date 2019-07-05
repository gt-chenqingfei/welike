package com.redefine.welike.common.util

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.google.android.gms.common.util.ClientLibraryUtils.getPackageInfo
import android.content.pm.PackageManager.GET_SIGNATURES
import com.redefine.welike.BuildConfig
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateEncodingException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

/**
 * keytool -list -v -keystore ~/work_space/welike.keystore
SHA1: 31:ED:A9:80:DB:0D:94:73:EB:F5:AF:60:BF:A6:E6:9E:49:98:4D:EE
 */
class SignatureUtil {
    val GoogleSign = "A4:1D:D5:1C:F1:59:C5:80:A4:35:B5:F2:94:AA:FC:EA:98:38:E2:55"
    val RedeSign = "31:ED:A9:80:DB:0D:94:73:EB:F5:AF:60:BF:A6:E6:9E:49:98:4D:EE"

    fun check(context: Context): Boolean {
        if (BuildConfig.DEBUG) return true
        val x = getCertificateSHA1Fingerprint(context)
        return (x == GoogleSign || x == RedeSign)
    }

    fun getCertificateSHA1Fingerprint(context: Context): String {
        //获取包管理器
        val pm = context.packageManager

        //获取当前要获取 SHA1 值的包名，也可以用其他的包名，但需要注意，
        //在用其他包名的前提是，此方法传递的参数 Context 应该是对应包的上下文。
        val packageName = context.packageName

        //返回包括在包中的签名信息
        val flags = PackageManager.GET_SIGNATURES

        var packageInfo: PackageInfo? = null

        try {
            //获得包的所有内容信息类
            packageInfo = pm.getPackageInfo(packageName, flags)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        //签名信息
        val signatures = packageInfo!!.signatures
        val cert = signatures[0].toByteArray()

        //将签名转换为字节数组流
        val input = ByteArrayInputStream(cert)

        //证书工厂类，这个类实现了出厂合格证算法的功能
        var cf: CertificateFactory? = null

        try {
            cf = CertificateFactory.getInstance("X509")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //X509 证书，X.509 是一种非常通用的证书格式
        var c: X509Certificate? = null

        try {
            c = cf!!.generateCertificate(input) as X509Certificate
        } catch (e: Exception) {
            e.printStackTrace()
        }

        var hexString: String? = null

        try {
            //加密算法的类，这里的参数可以使 MD4,MD5 等加密算法
            val md = MessageDigest.getInstance("SHA1")

            //获得公钥
            val publicKey = md.digest(c!!.getEncoded())

            //字节到十六进制的格式转换
            hexString = byte2HexFormatted(publicKey)

        } catch (e1: NoSuchAlgorithmException) {
            e1.printStackTrace()
        } catch (e: CertificateEncodingException) {
            e.printStackTrace()
        }

        return hexString ?: ""
    }

    //这里是将获取到得编码进行16 进制转换
    private fun byte2HexFormatted(arr: ByteArray): String {

        val str = StringBuilder(arr.size * 2)

        for (i in arr.indices) {
            var h = Integer.toHexString(arr[i].toInt())
            val l = h.length
            if (l == 1)
                h = "0$h"
            if (l > 2)
                h = h.substring(l - 2, l)
            str.append(h.toUpperCase())
            if (i < arr.size - 1)
                str.append(':')
        }
        return str.toString()
    }

}