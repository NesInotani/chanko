/*
 * Copyright (c) 2014 nobrooklyn
 *
 * ORIGINAL
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 8th713
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
angular.module('App', ["ngResource"])
.factory('Todo', function($resource) {
	return $resource('http://localhost:8080/chanko-angularjs/rs/todo/:id');
})
.service('todos', ['$rootScope', '$filter', function ($scope, $filter) {
	var list = [];

	$scope.$watch(function() {
		return list;
	}, function (value) {
		$scope.$broadcast('change:list', value);
	}, true);
	
	var where = $filter('filter');
	
	var done = { done: true };
	var remaining = { done: false };
	
	this.filter = {
		done: done,
		remaining: remaining
	};
	
	this.getDone = function () {
		return where(list, done);
	};
	
	this.add = function (title, done) {
		list.push({
			title: title,
			done: done
		});
	};
	
	this.remove = function (currentTodo) {
		list = where(list, function (todo) {
			return currentTodo !== todo;
		});
	};
	
	this.removeDone = function () {
		list = where(list, remaining);
	};
	
	this.changeState = function (state) {
		angular.forEach(list, function (todo) {
			todo.done = state;
		});
	};
}])
.controller('RegisterController', ['$scope', 'todos', function ($scope, todos) {
	$scope.newTitle = '';
	$scope.addTodo = function () {
		todos.add($scope.newTitle, false);
		$scope.newTitle = '';
	};
}])
.controller('ToolbarController', ['$scope', 'todos', function ($scope, todos) {
	$scope.filter = todos.filter;

	$scope.$on('change:list', function (evt, list) {
		var length = list.length;
		var doneCount = todos.getDone().length;
		
		$scope.allCount = length;
		$scope.doneCount = doneCount;
		$scope.remainingCount = length - doneCount;
	});
	
	$scope.checkAll = function () {
		todos.changeState(!!$scope.remainingCount);
	};
	
	$scope.changeFilter = function (filter) {
		$scope.$emit('change:filter', filter);
	};
	
	$scope.removeDoneTodo = function () {
		todos.removeDone();
	};
}])
.controller('TodoListController', ['$scope', 'todos', function ($scope, todos) {
	$scope.$on('change:list', function (evt, list) {
		$scope.todoList = list;
	});
	
	var originalTitle;
	
	$scope.editing = null;
	
	$scope.editTodo = function (todo) {
		originalTitle = todo.title;
		$scope.editing = todo;
	};
	
	$scope.doneEdit = function (todoForm) {
		if (todoForm.$invalid) {
			$scope.editing.title = originalTitle;
		}
		$scope.editing = originalTitle = null;
	};
	
	$scope.removeTodo = function (todo) {
		todos.remove(todo);
	};
}])
.controller('MainController', ['$scope', 'todos', 'Todo', function($scope, todos, Todo){
	Todo.query().$promise
		.then(function(datas) {
			angular.forEach(datas, function(todo) {
				todos.add(todo.title, todo.done);
			})
		});

	$scope.currentFilter = null;
	
	$scope.$on('change:filter', function (evt, filter) {
		$scope.currentFilter = filter;
	});
}])
.directive('mySelect', [function () {
	return function (scope, $el, attrs) {
		scope.$watch(attrs.mySelect, function (val) {
			if (val) {
				$el[0].select();
			}
		});
	};
}]);