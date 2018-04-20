# encoding: UTF-8
module Asciidoctor

  class JailHolder

    @@jail_location = nil

    def self.jail_location
      @@jail_location
    end

    def clear_jail_location
      @@jail_location = nil
    end

    def set_jail_location(jail_location)
      @@jail_location=jail_location
    end

  end

  module Git4CPathResolver

    def system_path(target, start = nil, jail = nil, opts = {})

      jail_location = JailHolder.jail_location

      unless jail_location
        raise ::SecurityError, "Jail location is null"
      end

      jail = posixify jail_location

      super(target, start, jail, opts)
    end

  end

  class PathResolver
    prepend Git4CPathResolver
  end

end
