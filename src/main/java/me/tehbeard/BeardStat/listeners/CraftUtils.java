package me.tehbeard.BeardStat.listeners;


public class CraftUtils {
/*
    //determine if grid houses valid recipe
    public boolean isValidShaped(ItemStack[] inventorycrafting,ShapedRecipe r) {
        
        //get recipe height and width
        int height = r.getShape().length;
        int width = 0;
        for(String s : r.getShape()){
            if(width< s.length()){
                width = s.length();
            }
        }
        
        //search crafting area, return true if valid shape found
        for (int i = 0; i <= 3 - width; ++i) {
            for (int j = 0; j <= 3 - height; ++j) {
                
                //check normal and mirrored ways
                if (isValidShaped(inventorycrafting,r, i, j, true)) {
                    return true;
                }

                if (isValidShaped(inventorycrafting,r, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isValidShaped(ItemStack[] inventorycrafting, ShapedRecipe r, int i, int j, boolean flag) {
        
      //get recipe height and width
        int height = r.getShape().length;
        int width = 0;
        for(String s : r.getShape()){
            if(width< s.length()){
                width = s.length();
            }
        }
        
        for (int k = 0; k < 3; ++k) {
            for (int l = 0; l < 3; ++l) {
                int i1 = k - i;
                int j1 = l - j;
                ItemStack itemstack = null;

                if (i1 >= 0 && j1 >= 0 && i1 < width && j1 < height) {
                    if (flag) {
                        
                        
                        itemstack = this.items[width - i1 - 1 + j1 * width];
                    } else {
                        itemstack = this.items[i1 + j1 * width];
                    }
                }

                ItemStack itemstack1 = inventorycrafting.b(k, l);

                if (itemstack1 != null || itemstack != null) {
                    if (itemstack1 == null && itemstack != null || itemstack1 != null && itemstack == null) {
                        return false;
                    }

                    if (itemstack.id != itemstack1.id) {
                        return false;
                    }

                    if (itemstack.getData() != -1 && itemstack.getData() != itemstack1.getData()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
*/
    
    
}
