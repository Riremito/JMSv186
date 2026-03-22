package odin.server;

public class StructPotentialItem {

    public byte incSTR, incDEX, incINT, incLUK, incACC, incEVA, incSpeed, incJump,
            incPAD, incMAD, incPDD, incMDD, prop, time, incSTRr, incDEXr, incINTr,
            incLUKr, incMHPr, incMMPr, incACCr, incEVAr, incPADr, incMADr, incPDDr,
            incMDDr, incCr, incDAMr, RecoveryHP, RecoveryMP, HP, MP, level,
            ignoreTargetDEF, ignoreDAM, DAMreflect, mpconReduce, mpRestore,
            incMesoProp, incRewardProp, incAllskill, ignoreDAMr, RecoveryUP;
    public boolean boss;
    public short incMHP, incMMP, attackType;
    public int potentialID, skillID;
    public int optionType, reqLevel; //probably the slot
    public String face; //angry, cheers, love, blaze, glitter

}
