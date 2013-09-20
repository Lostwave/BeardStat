<?php
require 'session.php';
require 'api/api.php';
?>
<!DOCTYPE html>
<html>
<head>
<script src="http://code.jquery.com/jquery-1.10.1.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.0.7/angular.min.js"></script>
<link href="https://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/css/bootstrap-combined.min.css" rel="stylesheet">
<link href="../style.css" rel="stylesheet">
<script src="https://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../js/PlayerHead.js"></script>
<script type="text/javascript">
$(function(){
  /*
  * Fill all the auto-complete datalists
  */
  $("datalist").each(function(idx){
    t = this;
    $.getJSON("getData.php?id=" + $(this).attr("id"),function(data){
      for(x in data){
        t.append('<option value="' + x + '">');
      }
    })
  });
});
/*
* Load scoreboard data manually
*/
function loadData(data){
  angular.element("body").scope().scoreboards = data;
  angular.element("body").scope().$apply();
}
//TODO - SAVE
//ALSO - STRIP ORDER DATA FOR NONE CLAUSE
</script>
<title>Scoreboards</title>
</head>
<body ng-app>
<datalist id="domain">
  <option value="default"/>
  <option value="spleefIt"/>
  <option value="beardach"/>
</datalist>
<datalist id="world">
  <option value="survival"/>
  <option value="nether"/>
  <option value="tolteca"/>
</datalist>
<datalist id="category">
</datalist>
<datalist id="statistic">
</datalist>
<div class="container">
<div class="row">
  <div ng-repeat="scoreboard in scoreboards">
    <h3>{{scoreboard.title}}</h3>
    id: <input type="text" ng-model="scoreboard.id"><br>
    <table class="table table-striped">
      <tr>
        <th>Label</th>
        <th>Domain</th>
        <th>World</th>
        <th>Category</th>
        <th>Statistic</th>
        <th>Order</th>
        <th>ASC/DESC</th>
      </tr>
      <tr ng-repeat="entry in scoreboard.data">
        <td><input class="input-medium" type="text" ng-model="entry.label"></td>
        <td><input list="domain" class="input-medium" type="text" ng-model="entry.domain"></td>
        <td><input list="world" class="input-medium" type="text" ng-model="entry.world"></td>
        <td><input list="category" class="input-medium" type="text" ng-model="entry.cat"></td>
        <td><input list="statistic" class="input-medium" type="text" ng-model="entry.stat"></td>
        <td><input class="input-small" type="text" ng-model="entry.order.idx"></td>
        <td><select class="input-small" ng-model="entry.order.type">
              <option value="NONE">None</option>
              <option value="ASC">Asc</option>
              <option value="DESC">Desc</option>
            </select>
        </td>
        
      </tr>
    </table>
    <hr>
  </div>
</div>
</div>
</body>
</html>