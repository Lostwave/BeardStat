<?php
require 'session.php';
require '../api/api.php';
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
  $("datalist").each(function(idx,ele){
    id = $(ele).attr("id");
    $.ajax({
  dataType: "json",
  url: "getData.php?id=" + id,
  cache: false,
  success: function(data){
      for(x in data){
        //console.log(id + " :: " + x);
        $(ele).append('<option value="' + x + '">');
      }
    }
  });
});
});


function loadDataAjax(){
  $.ajax({
    dataType:"json",
    url: '../config/scoreboards.json',
    cache: false,
    success: function(data){
    loadData(data);
  }});
}

function listCtrl($scope){
  $scope.tmpl = {};
  $scope.tmpl.scoreboard = {
    id:"newId",
    data: []
  };
  $scope.scoreboards = [];
  $scope.addScoreboard = function(){
    $scope.scoreboards.push(JSON.parse(JSON.stringify($scope.tmpl.scoreboard)));
  }

  $scope.addScoreboardField = function(idx){
    $scope.scoreboards[idx].data.push({});
  }
}

/*
* Load scoreboard data manually
*/
function loadData(data){
  angular.element("body").scope().scoreboards = data;
  angular.element("body").scope().$apply();
}

function saveData(){
  console.log("our side");
  console.log(angular.toJson(angular.element("body").scope().scoreboards,true));
  $.ajax('rest.php?id=scoreboards',{
    'data': angular.toJson(angular.element("body").scope().scoreboards,true), 
    'type': 'POST',
    'processData': false,
    success: function(data){console.log(data);},
    'contentType': 'application/json' //typically 'application/x-www-form-urlencoded', but the service you are calling may expect 'text/json'... check with the service to see what they expect as content-type in the HTTP header.

  });
}
//TODO - SAVE
//ALSO - STRIP ORDER DATA FOR NONE CLAUSE
</script>
<title>Scoreboards</title>
</head>
<body ng-app ng-controller="listCtrl">
<datalist id="domain">
</datalist>
<datalist id="world">
</datalist>
<datalist id="category">
</datalist>
<datalist id="statistic">
</datalist>
<div class="container">
<div class="row">
  <button ng-click="addScoreboard()">Add Item</button>
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
    <button ng-click="addScoreboardField($index)">Add Field</button>
    <hr>
  </div>
</div>
</div>
</body>
</html>