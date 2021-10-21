package com.teamwork.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Objects;

/**
 * @Author: Lsutin
 * @Date: 2021/10/20 8:51
 * @describe:
 */
@Data()
@Accessors(chain = true)
public class Fraction {//分数，分母只能为正数

    private int molecular;//分子
    private int denominator;//分母

    public Fraction(){

    }

    public Fraction(int molecular,int denominator){
        this.molecular=molecular;
        this.denominator=denominator;
    }

    public Fraction(String num){
        int i = num.indexOf('’');
        int integer=0, destMolecular=0, destDenominator=0;
        int index1=0;
        boolean flag=false;
        //存在 '’'符号
        if (i!=-1){
            String s = num.substring(0, i);
            integer=Integer.parseInt(s);
            if (integer<0){//负数
                flag=true;
                integer=-integer;
            }
            index1=i+1;
        }

        int index2 = num.indexOf('/');
        if (index2!=-1){//分子、分母
            destMolecular=Integer.parseInt(num.substring(index1,index2));
            destDenominator= Integer.parseInt(num.substring(index2+1));
        }else {
            destMolecular=Integer.parseInt(num);
            destDenominator=1;
        }
        this.denominator=destDenominator;
        this.molecular=destMolecular+integer*destDenominator;
        this.molecular=flag?-this.molecular:this.molecular;
    }

    /**
     * 整数转换为分数
     * @param num
     * @return
     */
    public static Fraction parseFraction(int num){
        Fraction fraction = new Fraction();
        fraction.setDenominator(1).setMolecular(num);
        return fraction;
    }

    /**
     * 生成随机分数
     * @param max
     * @return
     */
    public static Fraction newRandomFraction(int max){
        //分母
        int destDenominator = (int) (Math.random() * max);
        if (destDenominator==0){
            destDenominator=1;
        }
        //分子
        int destMolecular = (int) (Math.random() * (destDenominator * max));
        if (destMolecular==0){
            destMolecular=1;
        }

        return new Fraction(destMolecular, destDenominator);
    }

    /**
     * 加法
     * @param other
     */
    public void addition(Fraction other){
        int destDenominator = denominator * other.getDenominator();
        int destMolecular = molecular * other.getDenominator() + other.getMolecular() * denominator;
        //结果存放在原来的分数中
        molecular=destMolecular;
        denominator=destDenominator;
    }

    /**
     * 减法
     * @param other
     */
    public void subtraction(Fraction other){
        int destDenominator = denominator * other.getDenominator();
        int destMolecular = molecular * other.getDenominator() - other.getMolecular() * denominator;
        //结果存放在原来的分数中
        molecular=destMolecular;
        denominator=destDenominator;
    }

    /**
     * 乘法
     * @param other
     */
    public void multiplication(Fraction other){
        int destDenominator = denominator * other.getDenominator();
        int destMolecular = molecular * other.getMolecular();
        //结果存放在原来的分数中
        molecular=destMolecular;
        denominator=destDenominator;
    }

    /**
     * 除法
     * @param other
     */
    public void division(Fraction other){
        int destDenominator = denominator * other.getMolecular();
        int destMolecular = molecular * other.getDenominator();
        //结果存放在原来的分数中
        molecular=destMolecular;
        denominator=destDenominator;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        //先将负数转换为正数计算，最后再把负号加上
        boolean flag = molecular<0?true:false;
        int tempMolecular = molecular;
        if (flag){
            tempMolecular=-tempMolecular;
        }

        //计算带分数
        int integer = tempMolecular / denominator;//整数部分
        if (integer>0){
            builder.append(integer).append('’');
        }
        int destMolecular = tempMolecular % denominator;//真分数部分的分子
        if (destMolecular>0){
            builder.append(destMolecular).append('/').append(denominator);
        }else {
            builder.deleteCharAt(builder.length()-1);
        }

        if (flag){//如果是负数，把负号加上
            builder.insert(0,'-');
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fraction fraction = (Fraction) o;
        return (molecular == fraction.molecular &&
                denominator == fraction.denominator) ||
                ((double)molecular/denominator==(double)fraction.getMolecular()/fraction.getDenominator());
    }

    @Override
    public int hashCode() {
        return Objects.hash(molecular, denominator);
    }
}
