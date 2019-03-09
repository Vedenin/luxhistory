var app = angular.module("springDemo",[]);

app.controller("AppCtrl", function($scope, $http){
    $scope.statistics = [];
    $http({
        method: 'GET',
        url: 'http://localhost:9200/pop_nouns/_search?size=40&sort=_id&pretty=true&q=*:*'
    }).then(function (success){
        $scope.statistics = success.data;
    });
});