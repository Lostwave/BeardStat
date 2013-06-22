function loadPlayerHead(canvasObj){


  var jqe = $(canvasObj);
  var plr = jqe.data('name');
  jqe.attr('width',jqe.css('width'));
  jqe.attr('height',jqe.css('height'));
  console.log(plr);
  var img = new Image();

  var context = canvasObj.getContext('2d');
  var canvastest = false;
  for(x in context){
    if(x.indexOf("ImageSmoothingEnabled")!=-1){
      context[x] = false;
      canvastest = true;
    }
  }
  if(canvastest){
    img.onload = function() {
        // draw cropped image

        sx = 8;
        sy = 8;
        sw = 8;
        sh = 8;

        x = ((canvasObj.width/8) / 2);
        y = ((canvasObj.height / 8) / 2);
        w = canvasObj.width - ((canvasObj.width / 8));
        h = canvasObj.height - ((canvasObj.height / 8))
        
        context.drawImage(img,sx,sy,sw,sh,x,y,w,h);
        context.drawImage(img, 40, 8, 8, 8, 0, 0, canvasObj.width, canvasObj.height);
        console.log("drawing");
        
        
      };
      img.src = 'http://minecraft.net/skin/' + plr + '.png';
    }
    else
    {
      var img = $("<img>");
      img.attr('src','https://minotar.net/helm/' + plr);
      img.attr('class',jqe.attr('class'));
      jqe.replaceWith(img);
    }
  }
  $(function(){

    $('canvas').each(function(i,e){
      loadPlayerHead(e);
    });


  });