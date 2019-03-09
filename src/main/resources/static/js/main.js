var app = angular.module("springDemo",[]);

app.controller("AppCtrl", function($scope, $http){
    $scope.statistics = [];
    $http({
        method: 'GET',
        url: 'http://localhost:9200/pop_nouns/_search?pretty=true&q=*:*'
    }).then(function (success){
        $scope.statistics = success.results.hits.hits;
    });
});