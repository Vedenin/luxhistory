var app = angular.module("springDemo",[]);

app.controller("AppCtrl", function($scope, $http){
    $scope.websites = [];
    $http({
        method: 'GET',
        url: 'http://localhost:8888/api/stackoverflow'
    }).then(function (success){
        $scope.websites = success.data;
    });
});