(function () {
    'use strict';

    describe('Testing select2 configuration', function () {
        var obj, scope, config;

        beforeEach(module('seuss'));

        beforeEach(inject(function($rootScope, $controller) {
            scope = $rootScope.$new();

            scope.msg = function (arg) {
                return arg;
            };

            $controller('SchemaEditorCtrl', { $scope: scope });
            config = scope.SELECT_OBJECT_CONFIG;

            expect(config).not.toEqual(undefined);
        }));

        beforeEach(function () {
            obj = {
                name: 'test-name',
                module: 'test-module',
                namespace: 'test-namespace'
            };
        });

        it('Should set correct placeholder', function () {
            expect(config.placeholder).toEqual('seuss.info.selectObject');
        });

        it('Should correct format the selected object', function () {
            expect(config.formatSelection(obj))
                .toEqual('test-name (seuss.module: test-module  seuss.namespace: test-namespace)');

            delete obj.module;
            expect(config.formatSelection(obj))
                .toEqual('test-name (seuss.namespace: test-namespace)');

            delete obj.namespace;
            obj.module = 'test-module';
            expect(config.formatSelection(obj))
                .toEqual('test-name (seuss.module: test-module)');

            delete obj.module;
            delete obj.namespace;
            expect(config.formatSelection(obj)).toEqual('test-name');
        });

        it('Should return error message if label for a selected object cannot be created',
            function () {
            expect(config.formatSelection({})).toEqual('seuss.error');
            expect(config.formatSelection(null)).toEqual('seuss.error');
            expect(config.formatSelection(undefined)).toEqual('seuss.error');
        });

        it('Should correct format a object on list', function () {
            var strong = angular.element('<strong>').text(obj.name),
                name = angular.element('<div>').append(strong),
                module = angular.element('<span>')
                        .text(' {0}: {1}'.format(scope.msg('seuss.module'), obj.module)),
                namespace = angular.element('<span>')
                        .text(' {0}: {1}'.format(scope.msg('seuss.namespace'), obj.namespace)),
                info = angular.element('<div>').append(module).append(namespace),
                parent = angular.element('<div>').append(name).append(info);


            expect(config.formatResult(obj).wrapAll('<div></div>').parent().html())
                .toEqual(parent.wrapAll('<div></div>').parent().html());

            delete obj.module;
            info = angular.element('<div>').append(namespace);
            parent = angular.element('<div>').append(name).append(info);

            expect(config.formatResult(obj).wrapAll('<div></div>').parent().html())
                .toEqual(parent.wrapAll('<div></div>').parent().html());

            delete obj.namespace;
            obj.module = 'test-module';
            info = angular.element('<div>').append(module);
            parent = angular.element('<div>').append(name).append(info);

            expect(config.formatResult(obj).wrapAll('<div></div>').parent().html())
                .toEqual(parent.wrapAll('<div></div>').parent().html());

            delete obj.module;
            delete obj.namespace;
            parent = angular.element('<div>').append(name);

            expect(config.formatResult(obj).wrapAll('<div></div>').parent().html())
                .toEqual(parent.wrapAll('<div></div>').parent().html());
        });

        it('Should return error message if label for a object on list cannot be created',
            function () {
            expect(config.formatResult({})).toEqual('seuss.error');
            expect(config.formatResult(null)).toEqual('seuss.error');
            expect(config.formatResult(undefined)).toEqual('seuss.error');
        });

    });

}());
