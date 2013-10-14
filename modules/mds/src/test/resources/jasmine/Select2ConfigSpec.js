(function () {
    'use strict';

    describe('Testing select2 configuration', function () {
        var obj, scope, config;

        beforeEach(module('mds'));

        beforeEach(inject(function($rootScope, $controller) {
            scope = $rootScope.$new();

            scope.msg = function (arg) {
                return arg;
            };

            $controller('SchemaEditorCtrl', { $scope: scope });
            config = scope.SELECT_ENTITY_CONFIG;

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
            expect(config.placeholder).toEqual('mds.info.selectEntity');
        });

        it('Should correct format the selected entity', function () {
            expect(config.formatSelection(obj))
                .toEqual('test-name (mds.module: test-module  mds.namespace: test-namespace)');

            delete obj.module;
            expect(config.formatSelection(obj))
                .toEqual('test-name (mds.namespace: test-namespace)');

            delete obj.namespace;
            obj.module = 'test-module';
            expect(config.formatSelection(obj))
                .toEqual('test-name (mds.module: test-module)');

            delete obj.module;
            delete obj.namespace;
            expect(config.formatSelection(obj)).toEqual('test-name');
        });

        it('Should return error message if label for a selected entity cannot be created',
            function () {
            expect(config.formatSelection({})).toEqual('mds.error');
            expect(config.formatSelection(null)).toEqual('mds.error');
            expect(config.formatSelection(undefined)).toEqual('mds.error');
        });

        it('Should correct format a entity on list', function () {
            var strong = angular.element('<strong>').text(obj.name),
                name = angular.element('<div>').append(strong),
                module = angular.element('<span>')
                        .text(' {0}: {1}'.format(scope.msg('mds.module'), obj.module)),
                namespace = angular.element('<span>')
                        .text(' {0}: {1}'.format(scope.msg('mds.namespace'), obj.namespace)),
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

        it('Should return error message if label for a entity on list cannot be created',
            function () {
            expect(config.formatResult({})).toEqual('mds.error');
            expect(config.formatResult(null)).toEqual('mds.error');
            expect(config.formatResult(undefined)).toEqual('mds.error');
        });

    });

}());
