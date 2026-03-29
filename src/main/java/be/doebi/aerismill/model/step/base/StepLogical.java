package be.doebi.aerismill.model.step.base;


 public enum StepLogical {
        TRUE(0,"TRUE"),
        FALSE(1,"FALSE"),
        UNKNOWN(2,"UNKNOWN");
     private final int value;
     private final String description;

     StepLogical(int value,String description) {
         this.value = value;
         this.description = description;
     }

     public int getValue() {
         return value;
     }

     public String getDiscription(){
         return description;
     }
     @Override
     public String toString(){
         return description;
     }

     public boolean isTrue() {
         return this == TRUE;
     }

     public boolean isFalse() {
         return this == FALSE;
     }

     public boolean isUnknown() {
         return this == UNKNOWN;
     }
 }

